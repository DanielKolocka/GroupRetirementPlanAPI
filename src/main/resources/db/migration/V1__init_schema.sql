-- Employers
    CREATE TABLE employer (
        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
        name VARCHAR (50) NOT NULL,
        industry VARCHAR (50),
        createdAt TIMESTAMP DEFAULT now()
    );

-- Plans
    CREATE TYPE plan_type AS ENUM (
        'GROUP_RRSP', 'GROUP_TFSA'
        );

    CREATE TABLE plan (
        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
        employerId UUID NOT NULL REFERENCES employer(id),
        name VARCHAR (50) NOT NULL,
        planType plan_type NOT NULL,
        matchPercentage decimal(5,2) NOT NULL,
        matchCapPercentage decimal(5,2) NOT NULL,
        createdAt TIMESTAMP DEFAULT now()
    );

-- Members
    CREATE TABLE member(
        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
        employerId UUID NOT NULL REFERENCES employer(id),
        planID UUID NOT NULL REFERENCES plan(id),
        firstName VARCHAR (50) NOT NULL,
        lastName VARCHAR (50) NOT NULL,
        email VARCHAR (50) UNIQUE NOT NULL,
        annualSalary decimal(12,2) NOT NULL,
        enrollmentDate DATE DEFAULT CURRENT_DATE,
        createdAt TIMESTAMP DEFAULT now()
    );

-- Contributions
CREATE TYPE contribution_source AS ENUM (
        'EMPLOYEE', 'EMPLOYER_MATCH'
        );

    CREATE TABLE contribution(
        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
        memberId UUID NOT NULL REFERENCES member(id),
        amount decimal(12,2) CHECK ( amount > 0 ),
        source contribution_source NOT NULL,
        createdAt TIMESTAMP DEFAULT now()
    );

-- Indexes
CREATE INDEX idx_plan_employer_id ON plan(employerId);
CREATE INDEX idx_member_employer_id ON member(employerId);
CREATE INDEX idx_member_plan_id ON member(planID);
CREATE INDEX idx_contribution_member_id ON contribution(memberId);