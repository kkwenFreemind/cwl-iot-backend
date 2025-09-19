ALTER TABLE public.iot_device ADD emqx_username varchar(100) NULL;
ALTER TABLE public.iot_device ADD emqx_password varchar(100) NULL;
ALTER TABLE public.iot_device ADD mqtt_client_id varchar(100) NULL;
ALTER TABLE public.iot_device ADD telemetry_topic varchar(100) NULL;
ALTER TABLE public.iot_device ADD command_topic varchar(100) NULL;

-- Add comments for the new columns
COMMENT ON COLUMN public.iot_device.emqx_username IS 'EMQX MQTT broker authentication username';
COMMENT ON COLUMN public.iot_device.emqx_password IS 'EMQX MQTT broker authentication password';
COMMENT ON COLUMN public.iot_device.mqtt_client_id IS 'Unique MQTT client identifier for device connection';
COMMENT ON COLUMN public.iot_device.telemetry_topic IS 'MQTT topic for publishing device telemetry data';
COMMENT ON COLUMN public.iot_device.command_topic IS 'MQTT topic for receiving device control commands';
