# Inventario didáctico del proyecto Java / Orekit

Inventario elaborado mediante lectura del código fuente y de `pom.xml`. Describe lo que contienen las clases actuales; no acredita resultados de simulaciones ni una ejecución satisfactoria. No reproduce clases completas.

## 1. Alcance y entorno comprobados

El proyecto es un laboratorio de mecánica orbital: primero se plantea el fenómeno físico, después se calcula o propaga su evolución y finalmente se inspeccionan magnitudes con Java.

Hay **40 archivos Java**: 38 en `src/main/java` y 2 en `src/test/java`. Además de las 40 clases principales de esos archivos, existen dos clases anidadas y una interfaz anidada, descritas más adelante. La serie numerada presente va de `Ejemplo02` a `Ejemplo32`; no se ha localizado un `Ejemplo01`.

`pom.xml` declara Java 17, Orekit 12.0, Hipparchus 3.0 (core, geometry y ode), CZML Writer 3.0.0 y JUnit Jupiter mediante el BOM 5.11.0. Declara también Maven Compiler Plugin 3.13.0 y Surefire 3.3.0. Que una dependencia esté declarada no significa que todas las clases la utilicen.

| Temática / paquete | Archivos | Ubicación |
| --- | ---: | --- |
| Fundamentos orbitales: `com.roberto.orbit.basico` | 9 | `src/main/java/com/roberto/orbit/basico` |
| Seguimiento terrestre: `com.roberto.orbit.seguimiento` | 4 | `src/main/java/com/roberto/orbit/seguimiento` |
| Propagación: `com.roberto.orbit.propagacion` | 6 | `src/main/java/com/roberto/orbit/propagacion` |
| Eventos: `com.roberto.orbit.eventos` | 3 | `src/main/java/com/roberto/orbit/eventos` |
| Maniobras y ascenso: `com.roberto.orbit.maniobras` | 10 | `src/main/java/com/roberto/orbit/maniobras` |
| Experimentos iniciales: `com.roberto.orbit.pruebas` | 5 | `src/main/java/com/roberto/orbit/pruebas` |
| Configuración: `com.roberto.orbit.util` | 1 | `src/main/java/com/roberto/orbit/util` |
| Pruebas JUnit: `com.roberto.orbit.orekit_test` | 2 | `src/test/java/com/roberto/orbit/orekit_test` |

## 2. Lenguaje físico común

Los elementos orbitales clásicos son semieje mayor `a`, excentricidad `e`, inclinación `i`, argumento del perigeo `ω`, ascensión recta del nodo ascendente `Ω` (RAAN) y una anomalía que sitúa al satélite. Usamos **TA** para la anomalía verdadera, **E** para la excéntrica y **Mₑ** para la media. Algunas variables y salidas del código escriben `MA`; aquí se denominan Mₑ.

Los elementos describen la geometría y la posición sobre una cónica. Las coordenadas cartesianas `(r, v)` describen posición y velocidad mediante vectores. El sistema de referencia indica respecto a qué ejes se expresan: cambiar de representación orbital y cambiar de sistema de referencia son operaciones diferentes.

Orekit utiliza habitualmente metros, segundos y radianes; las masas se expresan en kg y los impulsos específicos en segundos. Para leer los resultados se convierte a km, km/s, minutos y grados. Un radio orbital se mide desde el centro terrestre; una altura se mide respecto a la superficie o al modelo de Tierra indicado.

En el problema kepleriano ideal se supone una atracción central de dos cuerpos. Una trayectoria perturbada añade otras aceleraciones. Sus elementos osculadores describen la cónica tangente al estado instantáneo y pueden variar con el tiempo; crear un `KeplerianOrbit` a partir de ese estado no elimina las perturbaciones que lo produjeron.

## 3. Fundamentos, representaciones y tipos de órbita

Paquete `com.roberto.orbit.basico`. Los enlaces conducen al código de cada clase.

| Clase | Conceptos físicos y cálculo que estudia | Conceptos de Java presentes |
| --- | --- | --- |
| [Ejemplo02OrbitaKepleriana](src/main/java/com/roberto/orbit/basico/Ejemplo02OrbitaKepleriana.java) | Construye una órbita con `a = 7000 km`, `e = 0,001`, `i = 51,6°`; obtiene elementos, periodo, movimiento medio y vectores `(r, v)` en EME2000. | `main`, variables `double`, constructores con `new`, métodos de consulta, objetos `Vector3D`, conversiones con `Math` y formato con `printf`. Contiene métodos privados de búsqueda y validación de datos, aunque `main` utiliza `OrekitConfig.inicializar()`. |
| [Ejemplo03OrbitaKepleriana](src/main/java/com/roberto/orbit/basico/Ejemplo03OrbitaKepleriana.java) | Añade el desplazamiento temporal kepleriano de 3600 s mediante `shiftedBy`; compara el estado inicial y posterior. | Referencias a objetos diferentes para cada instante, llamadas encadenadas y reutilización de la estructura del ejemplo inicial. |
| [Ejemplo04OrbitaKepleriana](src/main/java/com/roberto/orbit/basico/Ejemplo04OrbitaKepleriana.java) | Muestrea la evolución kepleriana y exporta posiciones a `orbita.csv` para su análisis exterior. | Bucle `for`, escritura con `FileWriter` y `PrintWriter`, cierre del escritor y tratamiento de `IOException` con `try/catch`. |
| [Ejemplo05ConversionRepresentacionOrbital](src/main/java/com/roberto/orbit/basico/Ejemplo05ConversionRepresentacionOrbital.java) | Expresa una misma órbita como `KeplerianOrbit` y `CartesianOrbit`; compara sus coordenadas de posición y velocidad. | Constructores de conversión, tipos de objeto distintos y acceso a `PVCoordinates` y `Vector3D`. |
| [Ejemplo06TransformacionFrames](src/main/java/com/roberto/orbit/basico/Ejemplo06TransformacionFrames.java) | Obtiene el estado en EME2000 y en ITRF. Introduce la diferencia entre un marco inercial y uno ligado a la Tierra, con transformación dependiente de la fecha. | Método auxiliar `imprimirEstado`, parámetros tipados y reutilización de la presentación de resultados. |
| [Ejemplo10VelocidadOrbital](src/main/java/com/roberto/orbit/basico/Ejemplo10VelocidadOrbital.java) | Estudia velocidades en perigeo, apogeo y TA de 90° y 270°; contrasta vis-viva, calcula tiempo hasta TA = 90°, componentes radial y transversal, momento angular y ángulo de trayectoria. | Operaciones vectoriales, producto escalar, normalización, `Math.sqrt`, `Math.atan2` y comparación de cálculos explícitos con consultas a Orekit. |
| [Ejemplo11EnergiaOrbital](src/main/java/com/roberto/orbit/basico/Ejemplo11EnergiaOrbital.java) | Compara la energía específica en perigeo y apogeo y reconstruye elementos desde `(r, v)`: `a`, `e`, `i`, RAAN, argumento del perigeo y TA, usando momento angular, vector nodal y vector excentricidad. | Productos vectoriales y escalares, descomposición en variables intermedias, trigonometría y condiciones `if` para ajustar ángulos negativos. |
| [Ejemplo27OrbitaEliptica](src/main/java/com/roberto/orbit/basico/Ejemplo27OrbitaEliptica.java) | Parte de radios de perigeo de 7000 km y apogeo de 20000 km; calcula `a`, `e`, velocidades y periodo. Tras una hora contrasta las anomalías, la ecuación de Kepler, radio, vis-viva y componentes de velocidad. | Traducción directa de fórmulas a expresiones `double`, consultas a objetos y comprobaciones numéricas impresas. |
| [Ejemplo28TiposDeOrbitas](src/main/java/com/roberto/orbit/basico/Ejemplo28TiposDeOrbitas.java) | Clasifica elipse, parábola e hipérbola mediante energía y excentricidad. A 7000 km compara velocidades circular y de escape; construye y desplaza una hipérbola, calcula velocidad asintótica, desviación y parámetro de impacto. Exporta `hiperbola.csv`. | Objetos orbitales, bucles, escritura de texto con `FileWriter`, `try` con recursos y tratamiento de errores de entrada/salida. La parábola se estudia con fórmulas; no se construye como `KeplerianOrbit`. |

Las relaciones físicas principales de este bloque son:

- Radios de ápsides: `rₚ = a(1 − e)`, `rₐ = a(1 + e)`.
- Movimiento medio y periodo elíptico: `n = √(μ/a³)`, `T = 2π√(a³/μ)`.
- Vis-viva: `v² = μ(2/r − 1/a)`; velocidad circular: `v꜀ = √(μ/r)`; escape: `vₑₛ꜀ = √(2μ/r)`.
- Energía mecánica específica kepleriana: `ε = v²/2 − μ/r = −μ/(2a)`, en J/kg o m²/s².
- Momento angular específico: `h = r × v`, en m²/s; para una elipse, `|h| = √(μa(1 − e²))`.
- Ecuación de Kepler elíptica: `Mₑ = E − e sin(E)`; `Mₑ(t) = Mₑ(t₀) + nΔt`.
- Componentes de velocidad: `vᵣ = (μ/|h|)e sin(TA)`, `vₜ = (μ/|h|)(1 + e cos(TA))`; ángulo de trayectoria `γ = atan2(vᵣ, vₜ)`.

Estas fórmulas corresponden al modelo de dos cuerpos. Reconstruir elementos con vectores requiere cuidado en órbitas circulares o ecuatoriales, donde ciertos ángulos clásicos no quedan definidos de forma única.

## 4. Seguimiento desde la superficie terrestre

Paquete `com.roberto.orbit.seguimiento`. El algoritmo transforma la geometría orbital a la perspectiva de una estación: Tierra elipsoidal WGS84, ubicación geodésica y sistema topocéntrico local. Los ejemplos utilizan Madrid, con latitud 40,4168°, longitud −3,7038° y altura 667 m.

| Clase | Conceptos físicos y cálculo que estudia | Conceptos de Java presentes |
| --- | --- | --- |
| [Ejemplo07SeguimientoDesdeMadrid](src/main/java/com/roberto/orbit/seguimiento/Ejemplo07SeguimientoDesdeMadrid.java) | Obtiene azimut, elevación y distancia del satélite desde Madrid en un instante mediante `OneAxisEllipsoid`, `GeodeticPoint` y `TopocentricFrame`. | Composición de objetos: la estación utiliza un punto geodésico y un modelo terrestre; paso de posición, frame y fecha a los métodos. |
| [Ejemplo08SeguimientoContinuoDesdeMadrid](src/main/java/com/roberto/orbit/seguimiento/Ejemplo08SeguimientoContinuoDesdeMadrid.java) | Calcula el seguimiento durante 120 minutos con muestreo de un minuto y evolución kepleriana mediante `shiftedBy`. | Bucle `for`, conversión de minutos a segundos, referencia de tipo `Orbit` y salida tabulada. |
| [Ejemplo09SeguimientoContinuoDesdeMadrid](src/main/java/com/roberto/orbit/seguimiento/Ejemplo09SeguimientoContinuoDesdeMadrid.java) | Usa una elipse con `a = 10000 km`, `e = 0,3` y Mₑ inicial de 30°. Añade evolución de TA, E y Mₑ, periodo, movimiento medio y conservación de `|h|`, además del seguimiento. | Dos bucles de muestreo, conversión explícita de tipo, producto vectorial y cálculos teóricos junto a resultados de la biblioteca. |
| [Ejemplo20VisibilidadEstacion](src/main/java/com/roberto/orbit/seguimiento/Ejemplo20VisibilidadEstacion.java) | Busca durante 24 h apariciones y desapariciones sobre el horizonte de elevación 0 con `ElevationDetector`; localiza máximos de elevación visibles con `ElevationExtremumDetector`. | Expresiones lambda como manejadores, condiciones según `increasing`, encadenamiento `withHandler` y devolución de `Action.CONTINUE`. |

El muestreo regular muestra una tabla de instantes elegidos; un detector busca el instante en que se cumple una condición. La elevación geométrica sobre el horizonte no incorpora por sí sola todos los requisitos de una comunicación real.

## 5. Propagación analítica, perturbaciones e integradores

Paquete `com.roberto.orbit.propagacion`.

Conviene separar tres responsabilidades: el **modelo de fuerzas** define la aceleración física; el **integrador** aproxima la solución de las EDO; `NumericalPropagator` **coordina** estado, integración, fuerzas y eventos. Cambiar de RK4 a DP853 cambia el método numérico; añadir J2 cambia el modelo físico.

| Clase | Conceptos físicos y cálculo que estudia | Conceptos de Java presentes |
| --- | --- | --- |
| [Ejemplo12KeplerianPropagator](src/main/java/com/roberto/orbit/propagacion/Ejemplo12KeplerianPropagator.java) | Propaga una órbita ideal durante 3600 s con `KeplerianPropagator` y obtiene un `SpacecraftState`. | Separación entre objeto órbita, propagador y estado; creación de una fecha futura y consulta del resultado. |
| [Ejemplo13PropagacionJ2](src/main/java/com/roberto/orbit/propagacion/Ejemplo13PropagacionJ2.java) | Compara Kepler con `EcksteinHechlerPropagator`, examina RAAN y argumento del perigeo y tabula RAAN hasta siete días; calcula una estimación secular de precesión nodal por J2. El proveedor solicitado es de grado 6 y orden 0: el caso perturbado incluye zonales hasta ese grado, no solo J2. | Uso de dos propagadores, proveedor de coeficientes, conversiones a `KeplerianOrbit`, bucle diario y comparación tabulada. |
| [Ejemplo14NumericalPropagator](src/main/java/com/roberto/orbit/propagacion/Ejemplo14NumericalPropagator.java) | Integra con DP853 en coordenadas cartesianas. Incorpora gravedad central, campo de grado 2 y orden 0, atracción del Sol y la Luna, arrastre con atmósfera exponencial y presión de radiación solar. Usa masa de 1000 kg, área de 20 m², `Cr = 1,2` y `Cd = 2,2`; compara estados y evolución de elementos. | Configuración de objetos mediante métodos `set...` y `addForceModel`, arrays de tolerancias `double[][]` y composición de varios modelos. |
| [Ejemplo15ComparacionPerturbaciones](src/main/java/com/roberto/orbit/propagacion/Ejemplo15ComparacionPerturbaciones.java) | Compara durante siete días casos acumulativos: central; J2; J2 + Sol; J2 + Sol + Luna; los anteriores + radiación; y finalmente + arrastre. Permite observar cómo cambia el resultado al ampliar el modelo. | Instancias independientes de integradores, propagadores y estados; reutilización de una órbita inicial y consulta de resultados de cada configuración. |
| [Ejemplo16ComparacionIntegradores](src/main/java/com/roberto/orbit/propagacion/Ejemplo16ComparacionIntegradores.java) | Mantiene gravedad central y compara siete días con DP853, RK4 de paso fijo de 10 s y solución kepleriana analítica; calcula diferencias de posición y velocidad. | Mismo coordinador con distintas clases de integrador; resta de vectores y normas para cuantificar errores. |
| [Ejemplo29PropagacionRealista](src/main/java/com/roberto/orbit/propagacion/Ejemplo29PropagacionRealista.java) | Parte de `a = 7000 km`, `e = 0,001`, `i = 51,6°`; integra central + J2 con DP853 y compara con evolución kepleriana tras una hora. Calcula aceleración perturbadora, estimación de variación del RAAN y separación espacial. El modelo de esta clase se limita a central + J2. | Construcción progresiva de un propagador, matrices de tolerancias y llamadas al modelo de fuerza y a los estados para comparar magnitudes. |

La ecuación central es `r̈ = −μr/|r|³`; en un modelo perturbado se añaden las aceleraciones de los modelos seleccionados. J2 representa el efecto dominante del achatamiento terrestre. Una aproximación secular utilizada en el estudio es `dΩ/dt ≈ −(3/2)n J2 (Rₑ/p)² cos(i)`, con `p = a(1 − e²)`, en rad/s.

Las diferencias respecto al caso kepleriano pueden deberse a física distinta; las diferencias entre integradores con las mismas fuerzas reflejan la aproximación numérica. Una tolerancia de posición usada para construir tolerancias del integrador no garantiza por sí sola ese error global al final de la propagación.

## 6. Detección de eventos orbitales

Paquete `com.roberto.orbit.eventos`. Se busca un cero de una función del estado: para los ápsides, `r · v = 0`; para el cruce ecuatorial, la coordenada perpendicular al plano ecuatorial cambia de signo.

| Clase | Conceptos físicos y cálculo que estudia | Conceptos de Java presentes |
| --- | --- | --- |
| [Ejemplo17EventosOrbitales](src/main/java/com/roberto/orbit/eventos/Ejemplo17EventosOrbitales.java) | Detecta perigeos y apogeos con `ApsideDetector` sobre propagación kepleriana y muestra la anomalía en el evento. El manejador activo continúa; las alternativas con `StopOnEvent` y `ContinueOnEvent` están comentadas. | Lambda con parámetros de estado, detector y sentido del cruce; `if`, conversión de la órbita y devolución de `Action.CONTINUE`. |
| [Ejemplo18CruceEcuador](src/main/java/com/roberto/orbit/eventos/Ejemplo18CruceEcuador.java) | Usa `NodeDetector` para distinguir nodos ascendentes y descendentes en una órbita kepleriana inclinada. | Registro del detector en el propagador, manejador lambda y bifurcación por `increasing`. |
| [Ejemplo19EventosConPropagadorNumerico](src/main/java/com/roberto/orbit/eventos/Ejemplo19EventosConPropagadorNumerico.java) | Lleva la detección de nodos a propagación numérica cartesiana con DP853 y campo terrestre de grado 2 y orden 0. | Composición de integrador, propagador, fuerza y detector; reutilización del esquema de manejo de eventos. |

En `ApsideDetector`, un cruce creciente corresponde al perigeo y uno decreciente al apogeo. En el detector de nodos, el sentido del cruce distingue ascenso y descenso respecto al plano del frame elegido. `Action.STOP` termina en el evento; `Action.CONTINUE` mantiene la propagación.

## 7. Maniobras impulsivas, transferencias y encuentro

Paquete `com.roberto.orbit.maniobras`, salvo el ascenso vertical descrito en el apartado siguiente.

Un impulso ideal cambia instantáneamente la velocidad: `v⁺ = v⁻ + Δv`, manteniendo la posición en ese instante. La órbita posterior se deduce del nuevo estado. El vector de impulso y su sistema de referencia importan tanto como su módulo; al interpretar `ImpulseManeuver` también interviene la actitud utilizada por el propagador.

| Clase | Conceptos físicos y cálculo que estudia | Conceptos de Java presentes |
| --- | --- | --- |
| [TransferenciaHohmann](src/main/java/com/roberto/orbit/maniobras/TransferenciaHohmann.java) | Encapsula las fórmulas de transferencia entre dos órbitas circulares coplanarias: semieje, excentricidad, dos incrementos de velocidad, coste total y semiperiodo de transferencia. | Clase reutilizable sin `main`, atributos privados, constructor, `this`, método `calcular()` y métodos de consulta `get...`. Hay que llamar a `calcular()` antes de consultar los resultados calculados. |
| [Ejemplo21ManiobraImpulsiva](src/main/java/com/roberto/orbit/maniobras/Ejemplo21ManiobraImpulsiva.java) | Aplica un primer impulso de 100 m/s programado con `DateDetector`; estudia cambios de perigeo y apogeo. Busca después un ápside y prepara un segundo impulso de 972,77 m/s. | Construcción de vectores con `normalize` y `scalarMultiply`, uso de `ImpulseManeuver`, lambda que devuelve `Action.STOP` y encadenamiento de propagadores. El detector de ápsides detiene en el primer ápside encontrado, sin filtrar explícitamente apogeo. |
| [Ejemplo22TransferenciaHohmann](src/main/java/com/roberto/orbit/maniobras/Ejemplo22TransferenciaHohmann.java) | Usa la clase auxiliar para transferir de 7000 a 14000 km; construye directamente la elipse de transferencia y la desplaza hasta el apogeo ideal. Calcula impulsos, pero no ejecuta aquí dos maniobras con `ImpulseManeuver`. | Instanciación de una clase propia, secuencia constructor–cálculo–consultas y uso de sus resultados en un objeto Orekit. |
| [Ejemplo23CambioPlanoOrbital](src/main/java/com/roberto/orbit/maniobras/Ejemplo23CambioPlanoOrbital.java) | Calcula el coste de cambiar 30° de plano, compara velocidades y representa el cambio como diferencia de vectores. Prepara un impulso tras 1 s usando la posición y velocidad en ese instante; inspecciona inclinación, RAAN, `a` y `e`. | Productos vectoriales, suma y resta de vectores, trigonometría, fechas de eventos y reconstrucción de la órbita final. |
| [Ejemplo24TransferenciaConCambioPlano](src/main/java/com/roberto/orbit/maniobras/Ejemplo24TransferenciaConCambioPlano.java) | Combina una transferencia de 7000 a 14000 km con cambio de plano de 30°. Compara circularización y cambio de plano separados frente a un impulso combinado, calcula el ahorro y prepara dos maniobras. | Reutilización de `TransferenciaHohmann`, composición vectorial, propagación por tramos y cálculo del segundo impulso a partir de velocidad deseada menos velocidad actual. |
| [Ejemplo25EncuentroOrbital](src/main/java/com/roberto/orbit/maniobras/Ejemplo25EncuentroOrbital.java) | Parte de dos satélites en la misma órbita circular de 7000 km separados 30°. Diseña una órbita de fase de dos vueltas; calcula el primer frenado, perigeo de la órbita de fase, distancia de encuentro y diferencia de velocidades. Programa el segundo impulso 1 s después del encuentro previsto. | Dos objetos orbitales, sincronización de fechas con `durationFrom`, resta de estados vectoriales y nuevo propagador desde el estado de encuentro. |
| [Ejemplo26EncuentroOrbital](src/main/java/com/roberto/orbit/maniobras/Ejemplo26EncuentroOrbital.java) | Repite el planteamiento de fase y coloca el segundo impulso en la fecha prevista del encuentro. Propaga desde la órbita original con ambos impulsos registrados y compara distancia y velocidad relativas finales. | Registro de dos eventos en un mismo propagador y comparación entre variantes. La distancia de referencia del ejemplo 25 está escrita como constante; no se ejecuta esa otra clase para obtenerla. |
| [Ejemplo30TransferenciaHohmann](src/main/java/com/roberto/orbit/maniobras/Ejemplo30TransferenciaHohmann.java) | Integra con DP853 y gravedad central la elipse de transferencia de 7000 a 14000 km durante el tiempo ideal; compara radio final y separación respecto a la solución analítica. Parte directamente de la elipse, sin simular los impulsos de salida y circularización. | Uso de la clase de cálculo propia junto a `NumericalPropagator`, tolerancias cartesianas y comparación vectorial de soluciones. |
| [Ejemplo31HohmannConJ2](src/main/java/com/roberto/orbit/maniobras/Ejemplo31HohmannConJ2.java) | Compara central frente a central + J2 para la misma elipse inicial ecuatorial. Evalúa estados al tiempo ideal y vuelve a propagar con un detector que se detiene en el apogeo real. Examina `r · v`, tiempo, radio, energía con corrección de potencial J2 y momento angular. | Dos integradores independientes, `resetInitialState`, registro posterior de detector, lambda con parada selectiva y cálculos a partir de estados. |

Para la transferencia de elevación utilizada (`r₂ > r₁`), las fórmulas son:

- `aₜ = (r₁ + r₂)/2`, `eₜ = |r₂ − r₁|/(r₁ + r₂)`.
- `v₁ = √(μ/r₁)`, `v₂ = √(μ/r₂)`.
- `vₜ₁ = √(μ(2/r₁ − 1/aₜ))`, `vₜ₂ = √(μ(2/r₂ − 1/aₜ))`.
- `Δv₁ = vₜ₁ − v₁`, `Δv₂ = v₂ − vₜ₂`, coste `|Δv₁| + |Δv₂|`.
- `tₜ = π√(aₜ³/μ)`: semiperiodo de la elipse ideal, aproximadamente 89,23 min para los radios de 7000 y 14000 km. Es una referencia teórica, no un resultado ejecutado para este inventario.

El cambio puro de plano a igual rapidez requiere `Δv = 2v sin(|Δi|/2)`. Si también cambia la rapidez, la diferencia vectorial da `Δv = √(vₐ² + vᵦ² − 2vₐvᵦ cos(Δi))`. De ahí el interés de combinar maniobras y realizarlas donde la velocidad es menor.

En el encuentro de los ejemplos 25 y 26, el periodo de fase se calcula como `Tfase = T₀(1 − 30/(360N))`, con `N = 2`. El semieje se obtiene invirtiendo la tercera ley de Kepler. Acercarse en posición e igualar la velocidad son requisitos distintos: ambas diferencias se inspeccionan en el código. Las fechas y vectores utilizados deben tenerse en cuenta al interpretar residuos; estos ejemplos no se presentan aquí como encuentros exactos verificados.

En el ejemplo 31, `v²/2 − μ/r` por sí sola no es la energía completa del modelo con J2. La corrección de potencial que calcula la clase se particulariza al plano ecuatorial del ejemplo; no debe extenderse sin cambios a cualquier latitud. El tiempo ideal de Hohmann tampoco identifica necesariamente el ápside de la trayectoria perturbada.

## 8. Ascenso vertical y EDO con Hipparchus

| Clase | Conceptos físicos y cálculo que estudia | Conceptos de Java presentes |
| --- | --- | --- |
| [Ejemplo32DespegueVertical](src/main/java/com/roberto/orbit/maniobras/Ejemplo32DespegueVertical.java) | Integra directamente un ascenso radial con empuje constante, masa variable y gravedad decreciente con la altura. Estado de tres componentes: altura, velocidad y masa. Integra hasta consumir el combustible previsto. | Constantes `private static final`, arrays, `ODEState`, `ExpandableODE`, DP853 y clase privada anidada que implementa una interfaz con métodos sobrescritos mediante `@Override`. |
| `Ejemplo32DespegueVertical.EcuacionesCohete` — clase anidada | Define las tres derivadas del sistema y devuelve dimensión 3. | `implements OrdinaryDifferentialEquation`, métodos `getDimension()` y `computeDerivatives(...)`; extrae componentes de `double[]` y devuelve otro array. |

El modelo es `dh/dt = v`, `dv/dt = F/m − μ/(Rₜ + h)²`, `dm/dt = −F/vₑ`. Usa masa inicial de 100000 kg, masa seca de 40000 kg, empuje de 1,5 MN y velocidad efectiva de eyección de 3000 m/s. El tiempo final se calcula como `(m₀ − mseca)vₑ/F = 120 s`.

Supone movimiento vertical, sin arrastre ni rotación terrestre, con empuje y velocidad de eyección constantes durante el intervalo integrado. No utiliza `NumericalPropagator`: Hipparchus integra directamente las ecuaciones. La masa seca determina el instante final calculado; no hay aquí un detector autónomo de agotamiento ni una fase posterior sin empuje.

En MATLAB/Octave, la función de derivadas tendría la forma `f(t,y)` y se entregaría a un integrador. Aquí la función se expresa mediante un objeto que cumple la interfaz `OrdinaryDifferentialEquation`. DP853 es un método adaptativo distinto del utilizado por `ode45`, aunque la idea de entregar estado inicial, derivadas y tolerancias es similar.

## 9. Experimentos de Java, tiempo e integración propia

Paquete `com.roberto.orbit.pruebas`. Pese al nombre del paquete, estas cinco clases están en `src/main/java`; no son pruebas JUnit.

| Clase o tipo | Conceptos físicos o numéricos | Conceptos de Java presentes |
| --- | --- | --- |
| [App](src/main/java/com/roberto/orbit/pruebas/App.java) | No contiene cálculo físico: imprime `Hello World!`. | Estructura mínima de una clase, paquete, `public static void main(String[] args)` y salida estándar. |
| [KeplerExample](src/main/java/com/roberto/orbit/pruebas/KeplerExample.java) | Comprueba la carga de datos, crea una fecha UTC y obtiene EME2000. Declara parámetros orbitales, pero no llega a construir ni propagar una órbita. | `File`, validación de directorio, excepción, registro de proveedor, bucle sobre nombres de datos cargados y conversiones angulares. Utiliza una ruta absoluta ya existente en el código. |
| [Time1](src/main/java/com/roberto/orbit/pruebas/Time1.java) | Compara representaciones UTC y TAI en torno al segundo intercalar del 31 de diciembre de 2005. Recorre 20 s con paso de 0,5 s. | Variables `final`, constructor privado, propiedades del sistema, bucle con `AbsoluteDate`, `compareTo`, `Locale.US`, `try/catch` y salida por error. Busca los datos en `orekit-data` dentro de la carpeta del usuario. |
| [RK1_4](src/main/java/com/roberto/orbit/pruebas/RK1_4.java) | Implementa integración explícita de EDO con selección de orden: Euler, RK2 de Heun, RK3 y RK4 clásico, mediante coeficientes y etapas. | Método estático, `switch`, arrays de una y dos dimensiones, bucles anidados, copias con `clone()`, `List`/`ArrayList`, genéricos y conversión de listas a arrays mediante streams. |
| `RK1_4.ODEFunction` — interfaz anidada | Representa la función de derivadas `f(t,y)`. | Interfaz de un único método `compute`, que permite pasar una lambda como argumento al integrador. |
| `RK1_4.Result` — clase anidada | Agrupa los tiempos y estados calculados: `tout` y `yout`. | Clase `static` anidada con campos públicos `double[]` y `double[][]`; en `yout`, cada fila corresponde a un instante. |
| [TestRK](src/main/java/com/roberto/orbit/pruebas/TestRK.java) | Integra `y′ = −2y`, `y(0) = 1`, entre 0 y 2 con paso 0,1 y RK4; imprime el resultado. La solución de referencia matemática es `y(t) = exp(−2t)`, aunque la clase no calcula automáticamente su error. | Lambda `(t, y) -> ...`, arrays, llamada a un método estático y recorrido de un objeto `Result`. Es un programa con `main`, no un test con aserciones. |

La función de derivadas de `RK1_4` equivale a una función que se pasa como argumento en MATLAB/Octave. `clone()` evita guardar varias referencias al mismo array mutable cuando se conserva el historial de estados. Una diferencia de indexación: en Java se empieza por cero; la primera componente es `y[0]`.

La lectura de `RK1_4` muestra que el ajuste del último paso se realiza después de calcular las etapas. No se ha corregido ni validado aquí ese comportamiento para intervalos que no sean múltiplos del paso; la existencia de la clase no acredita un integrador general verificado.

## 10. Configuración compartida de Orekit

| Clase | Función | Conceptos de Java presentes |
| --- | --- | --- |
| [OrekitConfig](src/main/java/com/roberto/orbit/util/OrekitConfig.java) | Imprime el directorio de trabajo, comprueba el directorio relativo `orekit-data`, vacía los proveedores del contexto por defecto y registra un `DirectoryCrawler`. Proporciona los datos externos necesarios para escalas de tiempo, orientación terrestre, gravedad y otros servicios que los ejemplos solicitan. | Clase `final`, constructor privado para impedir instancias, método estático `inicializar()`, comprobación con `File.isDirectory()` y `IllegalStateException` ante ausencia de datos. |

Esta inicialización se reutiliza en los ejemplos numerados. El código auxiliar antiguo de búsqueda por argumentos o variable de entorno que permanece en `Ejemplo02OrbitaKepleriana` no sustituye la ruta relativa utilizada por su `main`. Las clases iniciales y la prueba JUnit tienen estrategias propias de carga; se documentan tal como están, sin unificarlas ni cambiar rutas.

## 11. Pruebas automatizadas existentes

Paquete `com.roberto.orbit.orekit_test`, bajo `src/test/java`.

| Clase | Qué comprueba | Conceptos de Java y pruebas |
| --- | --- | --- |
| [AppTest](src/test/java/com/roberto/orbit/orekit_test/AppTest.java) | Solo ejecuta `assertTrue(true)`. Es una plantilla, sin validación orbital. | Anotación `@Test`, importación estática de aserciones y método de prueba JUnit 5. |
| [TestOrekit](src/test/java/com/roberto/orbit/orekit_test/TestOrekit.java) | Comprueba la existencia de `src/main/resources/orekit-data.zip`, lo registra con `ZipJarCrawler`, crea una órbita con `a = 7000 km`, `e = 0,01`, `i = 0,1 rad` y verifica esos tres elementos con tolerancia; comprueba que el vector posición no sea nulo. Utiliza `Constants.EIGEN5C_EARTH_MU`. | `@Test`, `assertTrue`, `assertEquals`, `assertNotNull`, tolerancias numéricas, carga desde ZIP y declaración `throws Exception`. |

Estas pruebas no verifican automáticamente transferencias, encuentros, precesión J2 ni resultados del ascenso vertical. Su presencia tampoco demuestra que se hayan ejecutado o superado en esta revisión.

## 12. Recorrido de estudio y consulta en Eclipse

Un recorrido progresivo es: `App` y carga de datos; fechas y frames; ejemplos 02–06; seguimiento 07–09; velocidad y energía 10–11; propagación 12–16; eventos 17–20; maniobras 21–26; síntesis de cónicas 27–28; integración y perturbaciones 29–31; ascenso vertical 32. `RK1_4` y `TestRK` permiten estudiar por separado el algoritmo numérico.

En Eclipse, abrir `RESUMEN_PROYECTO.md` en la raíz y localizar las clases en `src/main/java` mediante los paquetes indicados. Las clases con `main` se pueden lanzar con **Run As → Java Application**; las pruebas de `src/test/java`, con **Run As → JUnit Test**. `TransferenciaHohmann` y `OrekitConfig` se utilizan desde otras clases y no tienen un punto de entrada propio. Para los ejemplos que usan `OrekitConfig`, el directorio de trabajo debe permitir resolver el directorio relativo `orekit-data`.

Ejecutar `Ejemplo04OrbitaKepleriana` o `Ejemplo28TiposDeOrbitas` puede crear o sobrescribir sus CSV. No se han ejecutado esos ejemplos ni se ha compilado el proyecto para elaborar este documento: la verificación realizada es documental, de cobertura de clases y de correspondencia con el código fuente. No se han modificado clases, configuración, dependencias ni datos.
