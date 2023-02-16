# Mandelbrot Diver

Mandelbrot Diver is program that generates images of the Mandelbrot Set.
It has an intuitive GUI that allows the user to click and drag to zoom in, 
or copy-paste zoom coordinates from other sources.
The program stores the past several images generated, and allows the user to
undo and go back to earlier images.
It allows the user to define their own gradient color scheme,
and has several preset gradients and zoom locations.

## Run

To run, call the static void main(String[]) method in the MandelbrotDiver class.

## Details

The MandelbrotDiver class is the main frame and contains the GUI components and handles events.
The classes FractalModel and MandelbrotModel are responsible for generating the fractal images.
The classes OptionState, StateStack, ColorPreset, ZoomPreset, and Gradient handle the input data from the user.
The rest of the classes are custom GUI components.

## Author

Donovan Buterakos

## License

[MIT License](https://opensource.org/license/mit/)
