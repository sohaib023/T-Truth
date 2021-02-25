This repository contains code for second iteration of the labelling tool named T-Truth released under the following publication. The modifications include a major overhaul of the code, removing several major bugs and improving ease of use.

### Cite this tool as:
```Prof. Dr. Faisal Shafait, Table Ground Truth for the UW3 and UNLV datasets (DFKI-TGT-2010) ,1,ID:DFKI-TGT-2010_1,URL:http://tc11.cvc.uab.es/datasets/DFKI-TGT-2010_1```

### Launching T-Truth labelling tool:

    ./gtgui.sh

### Conversion of labelImg ground truth to T-Truth compatible xml files

        usage: python xml_unlv_converter.py [-h] -i INPUT -o OUTPUT

        optional arguments:
          -h, --help            show this help message and exit
          -i INPUT, --input INPUT
                                path to containing input xml files.
          -o OUTPUT, --output OUTPUT
                                path to store converted xml files.


### Instructions for labelling images:

    1- Open the image:
        - File -> Open Image
        or 
        - Ctrl + O
    2- Load table ground truth if it exists beforehand (else skip this step):
        - File -> Open Ground Truth
        or 
        - Ctrl + L
    3- Label the tables in the image.
        a- Select the tool:
            - Edit -> Mark Table.
            or
            - Ctrl + T.
        b- Click and drag the mouse to label the table.
        c- Once all tables are labelled move to next step.
    4- Label the Rows and Columns in the image.
        a- Select the tool:
            - Edit -> Mark Row/Cols.
            or
            - Ctrl + R.
        b- Click on a table to select it. (selected table will be highlighted).
        c- Identify the row seperators and press LEFT mouse button to mark them.
        d- Identify the column seperators and press RIGHT mouse button to mark them.
        e- Press Ctrl-Z to undo an incorrectly marked seperator.
        f- Repeat the process until all rows and columns of all tables have been marked. (Only move to the next step once you've ensured it.)    
    5- Convert the row-column seperators into cell boxes and refine the cell boxes:
        b- Select the Row/Column merging tool:
            - Edit -> Mark Row/Col Span
            or
            - Ctrl + M
        c- Click on a table to select it. (selected table will be highlighted).
        d- To mark two vertically adjacent cells as a single cell, press LEFT mouse button from the top cell and drag the mouse to the bottom cell.
        e- To mark two horizontally adjacent cells as a single cell, press RIGHT mouse button from the left cell and drag the mouse to the right cell.
        h- Repeat the process for all the tables. (steps 5.c onwards)
    6- Save the ground-truth file:
        - File -> Save Ground Truth 
        or 
        - Ctrl + S
