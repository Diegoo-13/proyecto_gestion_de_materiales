-- ============================================================
-- CORRECCIONES BD - PROYECTO GESTIÓN DE MATERIALES
-- ============================================================
-- Ejecutar en la base de datos: gestion_materiales
--
-- Correcciones realizadas durante las pruebas:
--   1. Materiales: stock_actual
--   2. Préstamos: id_material y cantidad
--   3. Ubicaciones reales del CGTI
--
-- IMPORTANTE: este script no elimina materiales ni préstamos.
-- ============================================================

-- ============================================================
-- 1. TABLA MATERIAL
-- ============================================================

ALTER TABLE public.material
ADD COLUMN IF NOT EXISTS stock_actual integer;

-- Si todavía existe stock_maximo, copiarlo a stock_actual
-- antes de eliminar la columna antigua.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'material'
          AND column_name = 'stock_maximo'
    ) THEN
        UPDATE public.material
        SET stock_actual = stock_maximo
        WHERE stock_actual IS NULL;

        ALTER TABLE public.material
        DROP COLUMN stock_maximo;
    END IF;
END $$;

-- ============================================================
-- 2. TABLA PRESTAMO
-- ============================================================

ALTER TABLE public.prestamo
ADD COLUMN IF NOT EXISTS id_material integer;

ALTER TABLE public.prestamo
ADD COLUMN IF NOT EXISTS cantidad integer;

-- ============================================================
-- 3. UBICACIONES DEL CGTI
-- ============================================================

-- Las ubicaciones 1, 2 y 3 eran datos de prueba. Se reutilizan
-- sus IDs para conservar las referencias de los materiales.
UPDATE public.ubicacion
SET nom_ubicacion = CASE id_ubicacion
    WHEN 1 THEN 'Laboratorio 1'
    WHEN 2 THEN 'Laboratorio 2'
    WHEN 3 THEN 'Laboratorio 3'
END
WHERE id_ubicacion IN (1, 2, 3);

-- Agregar las ubicaciones restantes si todavía no existen.
INSERT INTO public.ubicacion (nom_ubicacion)
SELECT v.nom_ubicacion
FROM (
    VALUES
        ('Laboratorio 4'),
        ('Laboratorio 5'),
        ('Laboratorio 6'),
        ('Laboratorio 7'),
        ('Audio Visual'),
        ('Recepción'),
        ('Área de Redes y Telecomunicaciones'),
        ('Área de Soporte Técnico')
) AS v(nom_ubicacion)
WHERE NOT EXISTS (
    SELECT 1
    FROM public.ubicacion u
    WHERE u.nom_ubicacion = v.nom_ubicacion
);

-- ============================================================
-- 4. VERIFICACIÓN
-- ============================================================

SELECT column_name, data_type
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'material'
ORDER BY ordinal_position;

SELECT column_name, data_type
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'prestamo'
ORDER BY ordinal_position;

SELECT id_ubicacion, nom_ubicacion
FROM public.ubicacion
ORDER BY id_ubicacion;

-- ============================================================
-- RESULTADO ESPERADO
-- ============================================================
-- MATERIAL: existe stock_actual y ya no existe stock_maximo.
-- PRESTAMO: existen id_material y cantidad.
-- UBICACION: existen las 11 ubicaciones reales del CGTI.
-- ============================================================
