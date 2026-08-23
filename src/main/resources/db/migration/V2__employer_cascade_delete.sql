-- Add ON DELETE CASCADE so deleting an employer cascades to its plans,
-- members, and contributions.

ALTER TABLE plan DROP CONSTRAINT plan_employer_id_fkey;
ALTER TABLE plan ADD CONSTRAINT plan_employer_id_fkey
    FOREIGN KEY (employer_id) REFERENCES employer(id) ON DELETE CASCADE;

ALTER TABLE member DROP CONSTRAINT member_employer_id_fkey;
ALTER TABLE member ADD CONSTRAINT member_employer_id_fkey
    FOREIGN KEY (employer_id) REFERENCES employer(id) ON DELETE CASCADE;

ALTER TABLE member DROP CONSTRAINT member_plan_id_fkey;
ALTER TABLE member ADD CONSTRAINT member_plan_id_fkey
    FOREIGN KEY (plan_id) REFERENCES plan(id) ON DELETE CASCADE;

ALTER TABLE contribution DROP CONSTRAINT contribution_member_id_fkey;
ALTER TABLE contribution ADD CONSTRAINT contribution_member_id_fkey
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;