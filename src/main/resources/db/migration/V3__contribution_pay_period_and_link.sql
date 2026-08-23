-- Add required pay period fields and self-referencing link from an
-- EMPLOYER_MATCH contribution back to the EMPLOYEE contribution that
-- generated it, plus tighten amount to NOT NULL to match the entity mapping.

ALTER TABLE contribution ADD COLUMN pay_period_start DATE NOT NULL DEFAULT CURRENT_DATE;
ALTER TABLE contribution ALTER COLUMN pay_period_start DROP DEFAULT;

ALTER TABLE contribution ADD COLUMN pay_period_end DATE NOT NULL DEFAULT CURRENT_DATE;
ALTER TABLE contribution ALTER COLUMN pay_period_end DROP DEFAULT;

ALTER TABLE contribution ADD COLUMN linked_contribution_id UUID REFERENCES contribution(id) ON DELETE CASCADE;

ALTER TABLE contribution ALTER COLUMN amount SET NOT NULL;