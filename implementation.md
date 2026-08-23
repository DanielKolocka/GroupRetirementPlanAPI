# Group Retirement Plan API — Build-From-Scratch Guide

You're building this yourself, so this document gives you the *what* and
*why* for every piece — data model, API behavior, business rules — without
writing the Java for you. Treat it as your spec sheet: refer back to it as
you build each piece, and use it to check your own work against expected
behavior.

---

## 1. Project overview

**What it is:** a backend API modeling a simplified group retirement plan.
Employers set up plans with contribution-matching rules. Members enroll and
submit contributions. The system automatically calculates the employer match,
tracks running balances, and projects retirement value.

**Why this project:** it mirrors Common Wealth's actual business domain
(employers, plans, members, advisors), gives you a real piece of non-trivial
business logic to design and defend (the match calculation), and exercises
the exact stack in the job posting — Java, Spring Boot, JPA/Hibernate,
PostgreSQL, Gradle, JUnit.

**Stack:** Java 17, Spring Boot 4.1.1, Spring Data JPA + Hibernate, PostgreSQL,
Flyway, Gradle (Groovy DSL), JUnit 5 + Mockito, Docker/Docker Compose.

---

## 2. Architecture recap

```
HTTP Request → Controller → Service → Repository → PostgreSQL
```

- **Controller** — HTTP concerns only (routes, status codes, request/response
  shape). No business logic.
- **Service** — all business logic and rules live here.
- **Repository** — pure data access via Spring Data JPA.
- **DTOs** (request/response objects) sit between Controller and everything
  else — controllers never expose JPA entities directly over the wire.

---

## 3. Data model

```
Employer 1───N Plan 1───N Member 1───N Contribution
```

### Employer
| Field | Type | Notes |
|---|---|---|
| id | UUID | primary key |
| name | String | required |
| industry | String | optional |
| createdAt | timestamp | set on creation |

### Plan
| Field | Type | Notes |
|---|---|---|
| id | UUID | primary key |
| employerId | UUID | FK → Employer, required |
| name | String | required |
| planType | enum | `DC_PENSION`, `GROUP_RRSP`, `GROUP_TFSA` |
| matchPercentage | decimal(5,2) | e.g. `50.00` = employer matches $0.50 per $1.00 contributed |
| matchCapPercentage | decimal(5,2) | e.g. `6.00` = employer matches up to 6% of the member's annual salary, per calendar year |
| createdAt | timestamp | |

### Member
| Field | Type | Notes |
|---|---|---|
| id | UUID | primary key |
| employerId | UUID | FK → Employer (denormalized for query convenience) |
| planId | UUID | FK → Plan, required |
| firstName / lastName | String | required |
| email | String | required, unique |
| annualSalary | decimal(12,2) | nullable, but required for match calculations to work |
| enrollmentDate | date | defaults to today |
| createdAt | timestamp | |

### Contribution
| Field | Type | Notes |
|---|---|---|
| id | UUID | primary key |
| memberId | UUID | FK → Member, required |
| amount | decimal(12,2) | must be > 0 |
| source | enum | `EMPLOYEE` or `EMPLOYER_MATCH` |
| payPeriodStart / payPeriodEnd | date | required |
| linkedContributionId | UUID | nullable, self-referencing FK — for an `EMPLOYER_MATCH` row, points back at the `EMPLOYEE` contribution that generated it |
| createdAt | timestamp | |

**Why the self-reference matters:** it's how you trace "this $500 match came
from that $1000 employee contribution" without a separate join table. When
you write the migration, this is a foreign key on `contribution` pointing
back at `contribution.id`.

**Indexes worth adding:** on each foreign key column (`plan.employer_id`,
`member.employer_id`, `member.plan_id`, `contribution.member_id`) — these
will be your most common lookup patterns.

---

## 4. API specification

For each endpoint: what it does, what it validates, and exactly what should
happen internally. Build your controllers/services to match this behavior —
this is your acceptance criteria.

### Employers

**`POST /api/employers`**
- Request body: `{ name (required, non-blank), industry (optional) }`
- Logic: create and persist a new Employer row
- Response: `201 Created` with the created employer (id, name, industry)
- Errors: `400` if `name` is blank

**`GET /api/employers/{id}`**
- Response: `200` with the employer, or `404` if no employer with that id exists

**`GET /api/employers`**
- Response: `200` with a list of all employers

**`DELETE /api/employers/{id}`**
- Logic: delete the employer. Because of the FK relationships, this should
  cascade — deleting an employer should delete its plans, which should
  delete their members, which should delete their contributions. Design your
  migration's `ON DELETE CASCADE` and your JPA `cascade` settings to make
  this actually true; test it explicitly.
- Response: `204 No Content`, or `404` if not found

### Plans

**`POST /api/employers/{employerId}/plans`**
- Request body: `{ name, planType, matchPercentage (0–100), matchCapPercentage (0–100) }`
- Logic: verify the employer exists, then create the plan linked to it
- Response: `201` with the created plan
- Errors: `404` if employer doesn't exist, `400` for validation failures

**`GET /api/employers/{employerId}/plans`**
- Response: `200` with all plans belonging to that employer

**`GET /api/plans/{id}`**
- Response: `200` with the plan, or `404`

### Members

**`POST /api/plans/{planId}/members`**
- Request body: `{ firstName, lastName, email (valid format, unique), annualSalary (positive) }`
- Logic: verify the plan exists, derive the employer from the plan (don't
  make the client pass employerId separately — get it from the plan
  relationship), create the member with today's enrollment date
- Response: `201` with the created member
- Errors: `404` if plan doesn't exist, `400` for validation failures

**`GET /api/employers/{employerId}/members`**
- Response: `200` with all members under that employer

**`GET /api/members/{id}`**
- Response: `200` with the member, or `404`

### Contributions — the core business logic

**`POST /api/members/{memberId}/contributions`**

Request body: `{ amount (positive), payPeriodStart (date), payPeriodEnd (date) }`

This single endpoint has the most interesting logic in the whole project.
Walk through it exactly like this:

1. Look up the member. `404` if not found.
2. Save a new `Contribution` row with `source = EMPLOYEE` for the submitted
   amount.
3. Calculate the **proposed match**:
   `proposedMatch = employeeAmount × (plan.matchPercentage / 100)`
4. Calculate the member's **annual cap in dollars**:
   `annualCapDollars = member.annualSalary × (plan.matchCapPercentage / 100)`
5. Sum how much `EMPLOYER_MATCH` has already been paid to this member
   **this calendar year** (query contributions where `source =
   EMPLOYER_MATCH` and `createdAt` falls within the current year).
6. `remainingCapRoom = annualCapDollars − alreadyMatchedThisYear`
7. `actualMatch = min(proposedMatch, remainingCapRoom)`, floored at zero
   (never negative)
8. If `actualMatch > 0`, save a second `Contribution` row: `source =
   EMPLOYER_MATCH`, amount = `actualMatch`, `linkedContributionId` pointing
   at the row from step 2. If `actualMatch` is zero, don't create a match
   row at all.

**Edge cases your implementation must handle correctly** — these are exactly
what your unit tests should cover:
- `matchPercentage = 0` → no match row created, ever
- Member has no `annualSalary` on file → no match row created (can't
  calculate a cap without it)
- Remaining cap room is fully used up → `actualMatch = 0`, no match row
- Remaining cap room is partially available → match is **capped at the
  remainder**, not the full proposed amount (this is the trickiest case —
  test it specifically)

Response: `201` with both contributions — something like
`{ employeeContribution: {...}, employerMatchContribution: {...} | null }`

Errors: `404` if member doesn't exist, `400` for validation (zero/negative
amount, missing dates)

**`GET /api/members/{memberId}/contributions`**
- Response: `200` with all contributions for that member, most recent
  pay period first

**`GET /api/members/{memberId}/balance`**
- Logic: sum all `EMPLOYEE` contributions ever, sum all `EMPLOYER_MATCH`
  contributions ever, total balance = both combined
- Response: `200` with `{ memberId, totalEmployeeContributions,
  totalEmployerMatch, totalBalance }`
- Errors: `404` if member doesn't exist

**`GET /api/members/{memberId}/projection?rate=0.05&years=20`**
- Query params optional, default `rate=0.05`, `years=20`
- Logic: `currentBalance = totalBalance` (from above), then
  `projectedValue = currentBalance × (1 + rate)^years`
- Response: `200` with `{ memberId, currentBalance, annualGrowthRate,
  yearsToRetirement, projectedValue }`
- This is intentionally the simplest calculation in the project — a good one
  to extend later (see Phase 3 ideas in the original implementation guide:
  accounting for future recurring contributions, not just the current
  lump sum)

---

## 5. Implementation guide — build order

Build in this order. Each step has a **definition of done** — a concrete way
to verify that step actually works before moving to the next one. Don't skip
ahead; each layer depends on the one before it compiling correctly.

### Step 1 — Configure `application.yml`
Set up `spring.datasource` (url/username/password pointing at a local
Postgres), `spring.jpa.hibernate.ddl-auto: validate`, `spring.flyway`
settings pointing at `classpath:db/migration`, and `server.port`.

**Done when:** the file exists and references environment variables with
sensible local defaults (don't hardcode a password you'll forget you set).

### Step 2 — Write the Flyway migration
Create `src/main/resources/db/migration/V1__init_schema.sql` and write the
`CREATE TABLE` statements for all four tables from Section 3, including
foreign keys, the `CHECK (amount > 0)` constraint on contribution, and
indexes on the FK columns.

**Done when:** the SQL is syntactically valid Postgres. You'll actually
verify this in Step 9 when Flyway runs it for real.

### Step 3 — Build the enums
Two simple enum files: `PlanType` (`DC_PENSION`, `GROUP_RRSP`,
`GROUP_TFSA`) and `ContributionSource` (`EMPLOYEE`, `EMPLOYER_MATCH`).

**Done when:** both compile with no dependencies on anything else.

### Step 4 — Build the entities
Build in dependency order: `Employer` first (nothing depends on it), then
`Plan` (references Employer), then `Member` (references Employer and Plan),
then `Contribution` (references Member, and self-references for the linked
match). Use the field tables in Section 3 exactly — field names should match
your migration's column names (accounting for Java camelCase ↔ SQL
snake_case, which `@Column(name = "...")` handles).

**Done when:** `./gradlew compileJava` succeeds with no errors. This doesn't
prove the schema is correct yet — that check comes in Step 9.

### Step 5 — Build the repositories
Four interfaces extending `JpaRepository<Entity, UUID>`. Add the specific
finder methods you'll need based on the API spec above — for example,
`PlanRepository` needs a way to find all plans for an employer, and
`ContributionRepository` needs a way to sum contributions by member, source,
and a date cutoff (this is what powers the match-cap calculation in Step 8).

**Done when:** they compile. You won't be able to fully test these in
isolation yet — that comes with the running app in Step 9.

### Step 6 — Build the DTOs
For each entity, a request record (what the client sends) and a response
record (what you send back) — plus a couple of purpose-built ones:
`ContributionSubmissionResult` (wraps both the employee and match
contribution together), `BalanceResponse`, `ProjectionResponse`. Add
validation annotations (`@NotBlank`, `@Positive`, `@Email`, etc.) on request
records per the "validates" notes in Section 4.

**Done when:** they compile and each field matches what Section 4 says the
request/response should contain.

### Step 7 — Build exception handling
One custom `NotFoundException` (extends `RuntimeException`), and one
`@RestControllerAdvice` class that maps `NotFoundException` → `404`,
`MethodArgumentNotValidException` (thrown automatically by `@Valid`) →
`400` with field-level error messages.

**Done when:** it compiles. You'll see it work for real once controllers
exist and you can trigger a validation failure via curl.

### Step 8 — Build the services (the real work)
Build in this order — each one only depends on the ones before it:

1. **EmployerService** — straightforward CRUD, no cross-entity logic.
2. **PlanService** — needs `EmployerService` to verify the employer exists
   before creating a plan.
3. **MemberService** — needs `PlanService` to verify the plan exists, and
   pulls the employer off the plan rather than taking it as separate input.
4. **ContributionService** — the hard one. Implement
   `submitEmployeeContribution()` following the 8-step algorithm in Section
   4 exactly. Write the match-calculation as its own method (something like
   `calculateEmployerMatch(member, plan, employeeAmount)`) returning just a
   `BigDecimal` — keeping it separate from the "save to database" logic
   makes it directly unit-testable without touching a real database, which
   matters for Step 11.

   **A note on `BigDecimal`:** don't use `double` for money anywhere in this
   project. `double` can't represent decimal fractions like `0.1` exactly,
   which causes real rounding bugs in financial math. `BigDecimal` with
   explicit scale (`setScale(2, RoundingMode.HALF_UP)`) is the correct tool
   here — if you haven't used it before, expect to look up
   `.multiply()`, `.divide()`, `.min()`, and `.compareTo()` (never use `==`
   or `.equals()` to compare BigDecimal amounts for "greater than" checks —
   `.compareTo()` is correct, `.equals()` also checks scale and will
   surprise you).

**Done when:** all four services compile, and you can trace through the
match algorithm by hand on paper for at least one example (e.g. $1000
contribution, 50% match, $6000 annual cap, $0 already matched → should
compute a $500 match) before writing the controller layer.

### Step 9 — Build the controllers
One controller per entity, following the routes and status codes exactly as
specified in Section 4. This is also the point where the whole stack
connects for the first time.

**Done when:** `docker compose up --build` succeeds and you see Flyway apply
your migration, then Hibernate validate it matches your entities (this is
where mismatches between Steps 2 and 4 surface — fix them here if so).

### Step 10 — Manual end-to-end testing
Using curl or Postman, walk through the full flow: create an employer,
create a plan on it, enroll a member, submit a contribution, check the
balance, get a projection. Then deliberately test the edge cases from
Section 4 — submit contributions that push a member past their match cap
across multiple requests, and confirm the match gets smaller and eventually
hits zero rather than erroring or over-matching.

**Done when:** every endpoint in Section 4 behaves exactly as specified,
including the error cases (try fetching a nonexistent employer, submitting
a contribution with a negative amount, etc.)

### Step 11 — Write unit tests for the match logic
Test `calculateEmployerMatch()` directly (not through the full HTTP flow) using
Mockito to fake the repository. Cover the four edge cases called out in
Section 4: normal match under the cap, match capped by remaining room, cap
fully exhausted, and zero match percentage. This is the same technique
covered earlier in this conversation (constructor injection making the
service mockable) — now you're applying it yourself.

**Done when:** `./gradlew test` passes, and you can explain what each test
is actually protecting against.

### Step 12 — Docker Compose polish
Confirm `docker-compose.yml` correctly wires Postgres and the app together
with a healthcheck so the app waits for Postgres to be ready before starting.

**Done when:** a clean `docker compose down -v && docker compose up --build`
(wiping the volume, starting fresh) works with zero manual steps.

---

## 6. Testing checklist

Before you consider this "interview ready," confirm you can answer yes to
all of these:

- [ ] Can you create an employer → plan → member → contribution chain via
  curl from an empty database, start to finish?
- [ ] Does submitting a contribution that exceeds the remaining match cap
  return a *partial* match, not a full one or an error?
- [ ] Does deleting an employer correctly cascade-delete everything beneath
  it?
- [ ] Do your unit tests fail if you deliberately comment out the cap check
  in `calculateEmployerMatch()`? (If not, the test isn't actually
  testing what you think it is.)
- [ ] Can you explain, without looking at your code, why DTOs exist
  separately from entities?

---

## 7. Git checkpoints

Commit after each step in Section 5, not all at once at the end — you want
commit history that shows a progression, both as a personal record of what
you built and because "walk me through your git history" is a fair
follow-up question in some interviews.

Suggested commit points: after Step 2 (schema), after Step 4 (entities),
after Step 6 (DTOs), after Step 8 (services — probably your biggest single
commit, since the match logic is the meat of the project), after Step 9
(working end-to-end app), after Step 11 (tests passing).
