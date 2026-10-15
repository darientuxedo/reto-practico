# Reto práctico — Kevin Alzate

Entrega de automatización funcional y evaluación no funcional de SauceDemo.
La rama `kevin-alzate` contiene el código, el informe y las evidencias en un solo commit.

## Entregables

- [Informe de pruebas](Informe-reto-Kevin-Alzate.pdf): resultados funcionales y no funcionales, hallazgos, análisis y limitaciones.
- [Proyecto y guía de ejecución](proyecto/README.md): Java 11, Gradle 8.0, Selenium, Cucumber y patrón Page Object.
- [Casos Gherkin](proyecto/src/test/resources/features): flujo positivo de compra, acceso rechazado y cancelación de checkout.
- [Datos Excel](proyecto/src/test/resources/data/test-data.xlsx): filas parametrizadas de los tres escenarios.
- [Evidencias funcionales](evidencias/funcional): informes Cucumber, Gradle y JUnit.
- [Evidencias Responsive](evidencias/calidad-web): capturas representativas y resumen verificado.
- [Auditorías Lighthouse de navegación](evidencias/lighthouse-completo/indice.html).
- [Auditorías Lighthouse del estado de confirmación](evidencias/lighthouse-confirmacion-estado/indice.html).

## Ejecución

Consulta los requisitos y la configuración de variables de entorno en
[proyecto/README.md](proyecto/README.md). Ejecuta los comandos desde `proyecto/`.
No se incluyen contraseñas privadas ni se cambia el Java global del equipo.

Para ver los reportes HTML, descarga o clona el repositorio y ábrelos localmente.
GitHub puede mostrar su código fuente en lugar de ejecutar la página.

## Resultados y alcance

La ejecución funcional documentada aprobó 3 escenarios y 12 pruebas unitarias.
Los resultados Responsive, Lighthouse y tiempos de interacción se documentan
por separado: no se presentan como mediciones realizadas por Selenium.

Las auditorías fallidas o parciales, la revisión visual pendiente y las diferencias
entre medición de carga y evaluación de estado se conservan en el informe.
Esta entrega no certifica toda la seguridad, accesibilidad ni compatibilidad del sitio.

No se almacena un ZIP duplicado: los archivos están disponibles en sus carpetas.
