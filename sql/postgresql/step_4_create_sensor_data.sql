CREATE TABLE sensor_data (
    time TIMESTAMPTZ NOT NULL,
    community_id INT NOT NULL,
    device_id TEXT NOT NULL,
    metric_name TEXT NOT NULL,
    metric_value DOUBLE PRECISION,
    metric_version INT,
    ingestion_time TIMESTAMPTZ DEFAULT NOW(),
    quality SMALLINT DEFAULT 0,
    -- 使用複合主鍵
    PRIMARY KEY (time, device_id, metric_name, community_id)
);

-- 建立 Hypertable
SELECT create_hypertable('sensor_data', 'time');

-- 建立關鍵索引
CREATE INDEX idx_sensor_data_community_time ON sensor_data (community_id, time DESC);
CREATE INDEX idx_sensor_data_device_time ON sensor_data (device_id, time DESC);

-- Add table comment
COMMENT ON TABLE sensor_data IS 'Stores time-series sensor data ingested from Sparkplug B payloads, partitioned by time for scalability and performance.';

-- Add column comments
COMMENT ON COLUMN sensor_data.time IS 'The timestamp when the sensor data was generated (from Sparkplug B payload), in UTC timezone.';
COMMENT ON COLUMN sensor_data.community_id IS 'Tenant identifier for multi-tenant isolation. Each community represents a separate customer or logical group.';
COMMENT ON COLUMN sensor_data.device_id IS 'Unique identifier of the device or node that produced the metric.';
COMMENT ON COLUMN sensor_data.metric_name IS 'Name of the measured metric (e.g., ''water_level'', ''temperature'', ''pressure'').';
COMMENT ON COLUMN sensor_data.metric_value IS 'Numeric value of the metric. NULL if the metric is non-numeric or not applicable.';
COMMENT ON COLUMN sensor_data.metric_version IS 'Version reference to metric_definitions.version for data lineage and schema evolution tracking.';
COMMENT ON COLUMN sensor_data.ingestion_time IS 'Timestamp when the record was inserted into the database. Used for monitoring ingestion latency and pipeline health.';
COMMENT ON COLUMN sensor_data.quality IS 'Quality indicator of the data point: 0=Good, 1=Uncertain, 2=Bad. Based on Sparkplug B quality definitions.';

-- Add index comments (optional but recommended for documentation)
COMMENT ON INDEX idx_sensor_data_community_time IS 'Index optimized for queries filtering by community_id and ordering by time descending (e.g., latest data per tenant).';
COMMENT ON INDEX idx_sensor_data_device_time IS 'Index optimized for queries filtering by device_id and ordering by time descending (e.g., device telemetry history).';