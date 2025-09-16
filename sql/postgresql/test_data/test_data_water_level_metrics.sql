-- IoT Metric Definitions Test Data for Water Level Monitoring
-- Department ID: 2 (Water Level Monitoring Department)
-- Created: 2025-09-16

-- Clean up existing test data for department 2 (optional - uncomment if needed)
-- DELETE FROM public.iot_metric_definitions WHERE dept_id = 2;

-- Insert 5 water level monitoring related metrics
INSERT INTO public.iot_metric_definitions (
    dept_id, metric_name, alias, physical_quantity, unit, data_type,
    is_active, version, created_at, updated_at
) VALUES
-- 1. Water Level Height - Primary measurement for water level monitoring
(2, 'WaterLevel', 'WL', 'Water Level', 'cm', 'Float',
 true, 1, NOW(), NOW()),

-- 2. Temperature Sensor - Environmental monitoring
(2, 'Temperature', 'TEMP', 'Temperature', 'Celsius', 'Float',
 true, 1, NOW(), NOW()),

-- 3. Battery Voltage - Device health monitoring
(2, 'BatteryVoltage', 'BAT_V', 'Electric Potential', 'Volt', 'Float',
 true, 1, NOW(), NOW()),

-- 4. Signal Strength - Communication quality monitoring
(2, 'SignalStrength', 'RSSI', 'Signal Strength', 'dBm', 'Int32',
 true, 1, NOW(), NOW()),

-- 5. Flow Rate - Water flow measurement
(2, 'FlowRate', 'FLOW', 'Volumetric Flow Rate', 'L/min', 'Float',
 true, 1, NOW(), NOW());

-- Verify the inserted data
SELECT
    id,
    dept_id,
    metric_name,
    alias,
    physical_quantity,
    unit,
    data_type,
    is_active,
    version,
    TO_CHAR(created_at, 'YYYY-MM-DD HH24:MI:SS') as created_at,
    TO_CHAR(updated_at, 'YYYY-MM-DD HH24:MI:SS') as updated_at
FROM public.iot_metric_definitions
WHERE dept_id = 2
ORDER BY id;

-- Summary statistics
SELECT
    COUNT(*) as total_metrics_dept_2,
    COUNT(CASE WHEN is_active = true THEN 1 END) as active_metrics,
    COUNT(CASE WHEN data_type = 'Float' THEN 1 END) as float_type_metrics,
    COUNT(CASE WHEN data_type = 'Int32' THEN 1 END) as int32_type_metrics
FROM public.iot_metric_definitions
WHERE dept_id = 2;

-- Expected output:
-- 5 records should be inserted with dept_id = 2
-- All records should have is_active = true
-- Data types should include: Float (4 records), Int32 (1 record)
-- Physical quantities should cover: Water Level, Temperature, Electric Potential, Signal Strength, Volumetric Flow Rate