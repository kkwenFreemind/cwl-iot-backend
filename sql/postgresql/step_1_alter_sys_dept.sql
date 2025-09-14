CREATE EXTENSION IF NOT EXISTS postgis;

-- 1. 新增原始經緯度欄位（用於前端顯示與簡單定位）
ALTER TABLE public.sys_dept
ADD COLUMN IF NOT EXISTS center_latitude DOUBLE PRECISION;

ALTER TABLE public.sys_dept
ADD COLUMN IF NOT EXISTS center_longitude DOUBLE PRECISION;

-- 2. 新增 PostGIS 空間欄位（用於空間運算與索引）
ALTER TABLE public.sys_dept
ADD COLUMN IF NOT EXISTS center_geom GEOGRAPHY(Point, 4326);

-- 3. 建立空間索引（加速「查哪些部門落在某區域內」等查詢）
CREATE INDEX IF NOT EXISTS idx_sys_dept_center_geom
ON public.sys_dept USING GIST (center_geom)
WHERE center_geom IS NOT NULL;

-- 4. 建立觸發器函數：自動同步經緯度 → center_geom
CREATE OR REPLACE FUNCTION sync_dept_center_latlng_to_geom()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.center_latitude IS NOT NULL AND NEW.center_longitude IS NOT NULL THEN
        NEW.center_geom = ST_SetSRID(ST_MakePoint(NEW.center_longitude, NEW.center_latitude), 4326)::GEOGRAPHY;
    ELSE
        NEW.center_geom = NULL;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 5. 建立觸發器：在 INSERT 或 UPDATE 時自動同步空間欄位
DROP TRIGGER IF EXISTS tr_sync_dept_center_geom ON public.sys_dept;

CREATE TRIGGER tr_sync_dept_center_geom
    BEFORE INSERT OR UPDATE OF center_latitude, center_longitude
    ON public.sys_dept
    FOR EACH ROW
    EXECUTE FUNCTION sync_dept_center_latlng_to_geom();