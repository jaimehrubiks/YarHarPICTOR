# Yar Har Pictor

-Yar Har Pictor- es una aplicación para flashear programas en la PICTOR de Sistemas Digitales, Telecomunicaciones (UPV/EHU) compatible con Windows, OSX y Linux. Utilizando una conexión serie mediante el integrado MAX232 y el programa KudeaPIC pregrabado en la ROM se envían los programas al microcontrolador PIC16###. Algunas de las características son las siguientes:

  - Búsqueda recursiva de programas compilados .hex
  - Automatización del grabado del programa sólo con dar a start y encender la PICTOR.
  - Protección añadida al pulsado involuntario del reset (~0.25s del circuito RC del reset + 3 segundos de YarHarPictor).
  - Protección contra el borrado o variación del fichero .hex en tiempo de conexión.

## Descarga

Enlace: https://github.com/jaimehrubiks/YarHarPICTOR/releases/download/v1.0/YarHarPictorv1_0_BETA.zip

## Imagen y Vídeo

<a href="https://www.youtube.com/watch?v=_tAKQwJ5mWk
" target="_blank"><img src="https://i.imgur.com/zhWHPzt.png" 
alt="IMAGE ALT TEXT HERE" width="1280" height="720" border="10" /></a>

## Modo de uso
1)  Se selecciona una carpeta con ficheros .hex se puede seleccionar una carpeta con muchas subcarpetas y proyectos, elevando el valor de "depth".

2) Se selecciona el puerto serie del ordenador.

3) Se elige el fichero .HEX

4) Se da click en "yar har fiddle di dee"

5) Se enciende la PICTOR o se pulsa el reset

## Autor
    Jaime Hidalgo García.
    Especial mención al progama KudeaPIC de la PICTOR UPV/EHU y al usuario de github / posible antiguo alumno Darkeye9 por la idea gracias a su script de línea de comandos para una versión de pictor.
    
## Código
  Compartiré el código quizá al acabar el curso, no quiero versiones alternativas que no funcionen correctamente.
