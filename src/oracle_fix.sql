-- SQL Script to fix ORA-00904 in Oracle Database
-- These commands add the missing columns to the existing tables.

-- Fix USER_ACCOUNT table
ALTER TABLE USER_ACCOUNT ADD (
    bio VARCHAR2(1000),
    profile_image_url VARCHAR2(500)
);

-- Fix ARTIST_ACCOUNT table
-- Note: 'bio' and 'status' might already exist, so we add the ones that are likely missing.
ALTER TABLE ARTIST_ACCOUNT ADD (
    security_question VARCHAR2(255),
    security_answer_hash VARCHAR2(255),
    password_hint VARCHAR2(255)
);

-- If ARTIST_ACCOUNT is also missing 'phone' (though not in entity, some versions might have it)
-- ALTER TABLE ARTIST_ACCOUNT ADD (phone VARCHAR2(20));

-- Verify the tables (Optional)
-- DESC USER_ACCOUNT;
-- DESC ARTIST_ACCOUNT;
