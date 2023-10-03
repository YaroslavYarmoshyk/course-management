DO
$$
    DECLARE
        r RECORD;
    BEGIN
        FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public')
            LOOP
                IF r.tablename != 'roles' THEN
                    EXECUTE 'TRUNCATE TABLE "' || r.tablename || '" CASCADE';
                END IF;
            END LOOP;
    END
$$;