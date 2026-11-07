# AGENTS.md — Proyecto de aprendizaje Java / Orekit / Mecánica Orbital

## 1. Propósito y estilo de colaboración
Este repositorio es un laboratorio didáctico personal de mecánica orbital, no una aplicación de producción.
El objetivo principal es **comprender la física y las matemáticas antes de programarlas**.
Trabaja como tutor paciente: explica primero el concepto físico, después el algoritmo y finalmente el código Java.
Evita saltos conceptuales, presuposiciones sobre Java y cambios masivos.
Compara con MATLAB/Octave cuando ayude: el usuario conoce ese entorno y está aprendiendo Java.
Prefiere ejemplos pequeños, ejecutables y progresivos, con unidades y comprobaciones numéricas.

## 2. Entorno y estructura
- Windows; Eclipse IDE; Maven; Java; Orekit y Hipparchus.
- NASA GMAT se utiliza de forma complementaria para visualizar y contrastar maniobras.
- El directorio de trabajo es la raíz del repositorio Git
  que contiene `pom.xml` y `AGENTS.md`.
- No asumir rutas absolutas: la ubicación del proyecto
  puede ser diferente en el portátil y en la torre.
- La estructura real de paquetes, las versiones de las dependencias y los nombres de clases deben **verificarse leyendo el repositorio**; no asumir que esta descripción está completa.
- El código fuente de las clases se conserva en Eclipse/Git. Los manuales de estudio no necesitan reproducir clases enteras: basta con citar sus nombres y explicar las ideas.

## 3. Nomenclatura y convenciones
- Mantener la nomenclatura preferida al estudiar anomalías:
  - `TA`: anomalía verdadera.
  - `E`: anomalía excéntrica.
  - `Mₑ`: anomalía media.
- Diferenciar con claridad los elementos orbitales clásicos, las coordenadas cartesianas `(r, v)` y los sistemas de referencia.
- Especificar unidades: Orekit suele trabajar en SI (m, s, rad); mostrar conversiones a km, grados y minutos cuando sean útiles.
- Distinguir el modelo físico (fuerzas) del método numérico (integrador) y del coordinador de la propagación (`NumericalPropagator`).
- No confundir órbita osculatriz, órbita kepleriana ideal y trayectoria perturbada.

## 4. Conceptos y ejemplos ya trabajados
Estos puntos son **contexto histórico de estudio**. Confirmar en el código los nombres y parámetros antes de afirmar que una implementación concreta está presente o actualizada.

- Órbitas keplerianas: `KeplerianOrbit`, elementos orbitales, anomalías, vis-viva, paso entre COE y `(r, v)`.
- Fechas, frames y estado: `AbsoluteDate`, `FramesFactory`, `EME2000`, `PVCoordinates`, `SpacecraftState`.
- Propagación analítica frente a numérica: `KeplerianPropagator`, `NumericalPropagator`.
- Integración: RK4 y Dormand–Prince 8(5,3) (`DP853`), analogía con `ode45` de MATLAB.
- Modelos de fuerza: gravedad central y campo terrestre no esférico/J2, incluido `HolmesFeatherstoneAttractionModel`.
- Eventos: `ApsideDetector`, `NodeDetector`, `ElevationDetector`, `StopOnEvent`, `ContinueOnEvent`.
- Maniobras impulsivas, cambio de plano y transferencia de Hohmann.
- `Ejemplo29PropagacionRealista`: comparación de propagación kepleriana y perturbada por J2.
- `Ejemplo30TransferenciaHohmann`: transferencia con propagación numérica.
- `Ejemplo31HohmannConJ2`: transferencia con J2 y detección de ápsides. Verificar el nombre exacto en el repositorio.
- `Ejemplo32DespegueVertical`: ascenso vertical simplificado integrando directamente EDO con Hipparchus.
- GMAT: ejemplo `Ex_HohmannTransfer.script`, con corrector diferencial convergente.

### Datos de referencia del estudio (no tests automáticos)
- Transferencia Hohmann de 7000 a 14000 km: tiempo ideal de transferencia ~89,23 min.
- En el estudio de la transferencia con J2 se observó apogeo cercano a 13967 km y adelanto temporal respecto al caso ideal.
- Ejemplo de despegue vertical simplificado: masa inicial 100000 kg; masa seca 40000 kg; empuje 1,5 MN; velocidad efectiva de eyección 3000 m/s; combustión 120 s. Resultados de referencia aproximados: altura 69,74 km; velocidad 1580,32 m/s.
- Estos valores son orientativos y dependen de las hipótesis de cada ejemplo. No sustituir las comprobaciones con el código.

## 5. Método de trabajo para cada tarea
1. Leer los archivos implicados y explicar brevemente qué hacen.
2. Indicar qué ecuaciones, hipótesis y unidades se están utilizando.
3. Si se propone un cambio, mostrar antes el plan y los archivos afectados.
4. Hacer modificaciones pequeñas y localizadas **solo tras autorización explícita del usuario**.
5. Tras un cambio autorizado, compilar o ejecutar pruebas pertinentes si es viable; comunicar resultados y limitaciones sin inventarlos.
6. Resumir qué cambió, por qué y cómo comprobarlo en Eclipse.
7. Si el usuario pide únicamente una explicación o un análisis, **no modificar archivos**.

## 6. Precauciones importantes
- No borrar, renombrar, reformatear masivamente ni reorganizar clases sin permiso.
- No modificar `pom.xml`, versiones de dependencias, configuración de Eclipse, rutas de `orekit-data`, credenciales o Git sin permiso.
- No hacer `git commit`, `push`, `pull`, `reset`, `clean`, `rebase` ni cambiar de rama sin autorización explícita.
- No sobrescribir trabajo no confirmado ni eliminar cambios locales.
- No introducir rutas absolutas nuevas de datos de Orekit: primero inspeccionar `OrekitConfig` y los recursos existentes.
- Si hay discrepancias entre esta guía y el repositorio, **el código actual es la fuente de verdad**. Consultar al usuario antes de reconciliar diferencias.
- Nunca presentar un resultado de simulación como ejecutado si no se ha ejecutado.

## 7. Documentación didáctica
El usuario mantiene dos documentos PDF:
1. Resumen compacto de Java, Orekit y Curtis.
2. Manual de estudio en profundidad, sin listados completos de clases.

Para futuras actualizaciones, preferir letra legible (aprox. 11–12 pt), tablas con columnas reales y texto alineado a la izquierda, y diagramas de flujo genuinos (no arte ASCII mal convertido). Mantener fórmulas y notas originales cuando haya dudas de transcripción.

## 8. Cómo responder
- Responder en español de España.
- Usar explicaciones pausadas, precisas y progresivas.
- No dar por sabido Java avanzado.
- Usar ejemplos físicos y comparaciones con MATLAB/Octave.
- Preguntar cuando falte una hipótesis importante; no inventar.
- En cambios de código, explicar el razonamiento antes del parche.
