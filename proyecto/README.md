# Reto de automatización web — Kevin Alzate

Tres flujos de SauceDemo con Java 11, Gradle 8.0, Selenium 4.41.0,
Cucumber 7.20.1, JUnit 4.13.2 y Apache POI 5.5.1. Patrón Page Object.
No utiliza Serenity ni modifica el Java global del equipo.

## Requisitos

- JDK 11 completo, no únicamente JRE.
- Chrome instalado. Firefox y Edge están soportados por configuración, pero requieren una ejecución propia para considerarlos validados.
- Acceso a SauceDemo, Maven Central y a la distribución oficial de Gradle durante la preparación.
- El wrapper incluye verificación SHA-256 de Gradle 8.0. Selenium Manager obtiene el driver compatible; puede indicarse uno local con `-Dwebdriver.chrome.driver=...`.

## Ejecución en Windows

Configura las contraseñas mediante variables de entorno de la sesión, sin escribirlas en Excel, código ni argumentos de Gradle:

| Hoja | Variable |
| --- | --- |
| Compra | `QA_SECRET_62D5143C073BA4` |
| UsuarioBloqueado | `QA_SECRET_7A671596D595E5` |
| Cancelacion | `QA_SECRET_3E30E33E19D81A` |

Para los usuarios de demostración que comparten contraseña, introdúcela una vez de forma oculta:

```powershell
$demoPassword = [Net.NetworkCredential]::new('', (Read-Host 'Contraseña pública de SauceDemo' -AsSecureString)).Password
'QA_SECRET_62D5143C073BA4','QA_SECRET_7A671596D595E5','QA_SECRET_3E30E33E19D81A' |
    ForEach-Object { [Environment]::SetEnvironmentVariable($_, $demoPassword, 'Process') }
Remove-Variable demoPassword

.\run-tests.ps1 -JavaHome 'C:\Java\jdk-11' -Browser chrome
```

Sustituye la ruta por tu instalación. El lanzador verifica JDK 11, aplica `JAVA_HOME`
y `Path` solo durante la ejecución y restaura sus valores incluso si hay un error.
No usa el perfil personal ni la sesión de Chrome del usuario.

- Solo contratos unitarios, sin contraseñas ni navegador:
  `.\run-tests.ps1 -JavaHome 'C:\Java\jdk-11' -UnitOnly`
- Ver la ejecución: añade `-ShowBrowser`.
- Cambiar navegador: `-Browser firefox` o `-Browser edge`.
- Alternativa, si la terminal ya usa JDK 11: `.\gradlew.bat clean test`.
- Un caso: `.\gradlew.bat test --tests com.ultimate.qa.RunCucumberTest "-Dcucumber.filter.tags=@scenario_3"`.
- En Linux/macOS: `JAVA_HOME=/ruta/jdk11 sh ./gradlew clean test`, con las variables de entorno previamente definidas. El uso de `sh` evita depender del permiso ejecutable al extraer el ZIP generado en Windows.

## Flujos y comprobaciones

| Caso | Acción principal | Resultado comprobado |
| --- | --- | --- |
| POS-001 | Finalizar la compra | Pantallas de catálogo y resumen, ruta de confirmación y mensaje exacto. |
| NEG-001 | Intentar acceso con cuenta bloqueada | Mensaje exacto de rechazo, ruta de login, botón de acceso visible y catálogo ausente. |
| ALT-001 | Cancelar el primer formulario de checkout | Formulario correcto antes de cancelar; retorno a la ruta y título del carrito; producto, cantidad y precio de la misma fila. |

El rechazo esperado es un caso negativo que **aprueba** si la aplicación lo aplica.
Comprobar el mensaje no se utiliza como sustituto de comprobar la pantalla.
La prueba de bloqueo no equivale a una auditoría completa de autorización del servidor.

## Datos Excel

`src/test/resources/data/test-data.xlsx` es la fuente de ejecución; se conservó
sin modificar respecto a la exportación recibida. Cada hoja tiene `caso_id` único.

1. Edita valores o añade filas con identificadores únicos en la hoja correspondiente.
2. Ejecuta de nuevo. `prepareFeatures` lee Excel con POI y reconstruye
   `build/generated-features` antes de que Cucumber descubra los escenarios.
3. `Examples` contiene los identificadores de casos. El Given selecciona la fila.
   Las referencias `{{Compra.producto}}`, por ejemplo, las resuelve el código
   Java contra esa hoja y fila; no son una función nativa de Cucumber.

No hay valores duplicados en Examples que deban mantenerse manualmente.
Una referencia calificada de otra hoja se rechaza, aunque ambas tengan una columna con el mismo nombre.
Para otro libro compatible: `gradlew.bat test "-Ddata.file=C:/datos/test-data.xlsx"`.
No se conecta automáticamente al archivo original usado por Ultimate.

No uses fórmulas ni celdas con error. Conserva códigos postales y otros identificadores
con ceros iniciales como texto. Las contraseñas incluidas son referencias a variables,
no sus valores. Los datos no sensibles también deben revisarse antes de publicar.

## Estructura y manejo de errores

- `pages/`: acciones y comprobaciones de cada pantalla. `RecordedPage` concentra
  localización estricta, esperas explícitas y acciones nativas.
- `steps/`: Gherkin enlazado a objetos de página, con nombres de métodos legibles.
- `support/`: navegador independiente por escenario, selección de datos y preparación de features.
- `*Test.java`: contratos unitarios de datos, escape de XPath, unicidad y comprobación exacta de precios.

No se elige el primer elemento cuando un selector coincide con varios. Los
alternativos solo pueden converger en un elemento; un conflicto detiene la prueba.
No se fuerzan clics JavaScript ni se repite una acción para ocultar su fallo.
Las esperas pueden volver a localizar un elemento si el DOM se reconstruye.

Un fallo mantiene la prueba en rojo. Se intenta adjuntar captura y siempre se
liberan el navegador y la fila de datos. El resultado lo determina JUnit/Cucumber,
no un mensaje manual de éxito.

## Informes y diagnóstico

- `build/reports/cucumber.html`: informe de los tres flujos.
- `build/reports/cucumber.json` y `cucumber.xml`: resultados procesables.
- `build/reports/tests/test/index.html`: resultado de toda la suite, incluidas las pruebas unitarias.
- `build/test-results/test/`: XML de JUnit.

Los reportes se regeneran en cada ejecución. Conserva los que acompañan la entrega
antes de lanzar otra corrida. Las capturas y datos no sensibles pueden mostrar
información del sitio; revisa las evidencias antes de compartirlas.

Si falta una variable, se indica su nombre y no se imprime su valor.
Para más diagnóstico de Gradle: `gradlew.bat test --stacktrace`.
La compatibilidad DevTools/CDP de Chrome no se usa en estos casos: la suite utiliza
WebDriver. Una advertencia de CDP no demuestra un fallo funcional ni una auditoría
de red completada.

## Alcance de la entrega

Esta suite cubre los tres flujos descritos, no todos los comportamientos del sitio.
Los resultados Responsive, Lighthouse y tiempos de interacción se entregan por
separado; no se presentan como mediciones realizadas por este proyecto Selenium.
Un cambio de tamaño de ventana no representa un dispositivo físico.

El ZIP original se conserva aparte. No publiques secretos, `.gradle/` ni `build/`.
El informe adjunto identifica las ejecuciones reales y sus limitaciones; esta
revisión no es una certificación de seguridad ni de compatibilidad universal.
