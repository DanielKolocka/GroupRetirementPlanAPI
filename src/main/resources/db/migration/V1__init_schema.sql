-- Employers
    CREATE TABLE employer (
        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
        name VARCHAR (50) NOT NULL,
        industry VARCHAR (50),
        created_at TIMESTAMP NOT NULL DEFAULT now()
    );

-- Plans
    CREATE TYPE plan_type AS ENUM (
        'GROUP_RRSP', 'GROUP_TFSA'
        );

    CREATE TABLE plan (
        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
        employer_id UUID NOT NULL REFERENCES employer(id),
        name VARCHAR (50) NOT NULL,
        plan_type plan_type NOT NULL,
        match_percentage decimal(5,2) NOT NULL,
        match_cap_percentage decimal(5,2) NOT NULL,
        created_at TIMESTAMP NOT NULL DEFAULT now()
    );

-- Members
    CREATE TABLE member(
        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
        employer_id UUID NOT NULL REFERENCES employer(id),
        plan_id UUID NOT NULL REFERENCES plan(id),
        first_name VARCHAR (50) NOT NULL,
        last_name VARCHAR (50) NOT NULL,
        email VARCHAR (50) UNIQUE NOT NULL,
        annual_salary decimal(12,2) NOT NULL,
        enrollment_date DATE NOT NULL DEFAULT CURRENT_DATE,
        created_at TIMESTAMP NOT NULL DEFAULT now()
    );

-- Contributions
CREATE TYPE contribution_source AS ENUM (
        'EMPLOYEE', 'EMPLOYER_MATCH'
        );

    CREATE TABLE contribution(
        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
        member_id UUID NOT NULL REFERENCES member(id),
        amount decimal(12,2) CHECK ( amount > 0 ),
        source contribution_source NOT NULL,
        created_at TIMESTAMP DEFAULT now()
    );

-- Indexes
CREATE INDEX idx_plan_employer_id ON plan(employer_id);
CREATE INDEX idx_member_employer_id ON member(employer_id);
CREATE INDEX idx_member_plan_id ON member(plan_iD);
CREATE INDEX idx_contribution_member_id ON contribution(member_id);