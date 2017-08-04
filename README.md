# NASARSSIR
**NASARSSI**mage**R**eader checks the NASA daily image feed for new images, and stores them to given directory.

Stops if any image already exists, starting from last because of the RSS feed build.

###*Security of link/url is* ***your*** *responsibility*

## Usage

```
 java NASARSSIR PATH
 where:
 ~PATH  -  is the (preferred absolute) path to where you want to store
 ```

## Build Dependencies

*  rome-1.0 (https://rometools.github.io/rome/)
*  jdom-1.1.3 (http://www.jdom.org/)