CREATE TABLE IF NOT EXISTS report (
    id SERIAL PRIMARY KEY,
    content TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE OR REPLACE FUNCTION notify_report_event()
RETURNS trigger AS $$
BEGIN
  PERFORM pg_notify('report_channel', row_to_json(NEW)::text);
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_trigger WHERE tgname = 'report_trigger'
    ) THEN
        CREATE TRIGGER report_trigger
        AFTER INSERT OR UPDATE ON report
        FOR EACH ROW
        EXECUTE FUNCTION notify_report_event();
    END IF;
END;
$$;
