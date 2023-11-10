[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/YybNWfh8)







Task:

Your application will take three arguments from the command line: file name, square size and the processing mode (Example: yourprogram somefile.jpg 5 S):

file name: the name of the graphic file of jpg format (no size constraints)
square size: the side of the square for the averaging
processing mode: 'S' - single threaded and 'M' - multi threaded
Your task is to show the image, and start performing the following procedure:

from left to right, top to bottom find the average color for the (square size) x (square size) boxes and set the color of the whole square to this average color. You need to show the result by progress, not at once.
In the multi-processing mode, you need to perform the same procedure in parallel threads. The number of threads shall be selected according to the computer's CPU cores.
There result shall be saved in a result.jpg file. The result of the processing shall look like the attached example.

The evaluation will consider the following criteria:

readability
coding style
documentation (README file is ok)
portability (shall run on any pc with the provided instructions)


https://stackoverflow.com/questions/76097556/homebrew-on-mac-not-using-the-edited-opencv-rb-file-via-brew-edit-opencv-during