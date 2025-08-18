CREATE OR REPLACE FUNCTION update_sys_update_time()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.sys_update_time = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;