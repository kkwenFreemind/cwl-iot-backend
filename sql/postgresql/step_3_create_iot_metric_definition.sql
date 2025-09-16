CREATE TABLE public.iot_metric_definitions (
	id serial4 NOT NULL,
	dept_id bigserial NOT NULL,
	metric_name text NOT NULL,
	alias text NULL,
	physical_quantity text NOT NULL,
	unit text NOT NULL,
	data_type text NOT NULL,
	is_active bool DEFAULT true NOT NULL,
	"version" int4 DEFAULT 1 NOT NULL,
	created_at timestamptz DEFAULT now() NOT NULL,
	updated_at timestamptz DEFAULT now() NOT NULL,
	CONSTRAINT iot_metric_definitions_data_type_check CHECK ((data_type = ANY (ARRAY['Int8'::text, 'Int16'::text, 'Int32'::text, 'Int64'::text, 'UInt8'::text, 'UInt16'::text, 'UInt32'::text, 'UInt64'::text, 'Float'::text, 'Double'::text, 'Boolean'::text, 'String'::text]))),
	CONSTRAINT iot_metric_definitions_dept_id_metric_name_key UNIQUE (dept_id, metric_name),
	CONSTRAINT iot_metric_definitions_pkey PRIMARY KEY (id),
	CONSTRAINT iot_metric_definitions_dept_id_fkey FOREIGN KEY (dept_id) REFERENCES public.sys_dept(id) ON DELETE CASCADE
);


-- Creating indexes to optimize query performanceAA
CREATE INDEX idx_metric_definitions_community ON iot_metric_definitions (dept_id);
CREATE INDEX idx_metric_definitions_name ON iot_metric_definitions (metric_name);
CREATE INDEX idx_metric_definitions_alias ON iot_metric_definitions (alias) WHERE alias IS NOT NULL;

-- Adding comments for documentation
COMMENT ON TABLE iot_metric_definitions IS 'Stores payload metric definitions for each community, adhering to Sparkplug B specification';
COMMENT ON COLUMN iot_metric_definitions.id IS 'Unique identifier for the metric definition';
COMMENT ON COLUMN iot_metric_definitions.dept_id IS 'Foreign key referencing the dept (tenant) this metric belongs to';
COMMENT ON COLUMN iot_metric_definitions.metric_name IS 'Standard metric name as defined in Sparkplug B payload, unique within a community';
COMMENT ON COLUMN iot_metric_definitions.alias IS 'Optional alias for compatibility with legacy devices or alternative naming conventions, unique within a community if specified';
COMMENT ON COLUMN iot_metric_definitions.physical_quantity IS 'Human-readable description of the physical quantity measured (e.g., Water Level)';
COMMENT ON COLUMN iot_metric_definitions.unit IS 'Unit of measurement for the metric (e.g., cm, V, dBm)';
COMMENT ON COLUMN iot_metric_definitions.data_type IS 'Data type of the metric, constrained to Sparkplug B compatible types (e.g., Int32, Float, String)';
COMMENT ON COLUMN iot_metric_definitions.is_active IS 'Indicates whether the metric is currently active or deprecated';
COMMENT ON COLUMN iot_metric_definitions.version IS 'Version number to track changes to the metric definition';
COMMENT ON COLUMN iot_metric_definitions.created_at IS 'Timestamp when the metric definition was created';
COMMENT ON COLUMN iot_metric_definitions.updated_at IS 'Timestamp when the metric definition was last updated';