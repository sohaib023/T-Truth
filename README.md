# Link to original Dataset and Tool:
http://tc11.cvc.uab.es/datasets/DFKI-TGT-2010_1

# Launching T-Truth labelling tool:

    ./gtgui.sh

# Conversion of original xml ground truth to T-Truth compatible xml files

        usage: python xml_unlv_converter.py [-h] -i INPUT -o OUTPUT

        optional arguments:
          -h, --help            show this help message and exit
          -i INPUT, --input INPUT
                                path to containing input xml files.
          -o OUTPUT, --output OUTPUT
                                path to store converted xml files.


# Instructions for labelling images:

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
        a- Edit -> Evaluate initial cells 
            Note: Once this step is taken you cannot go back to adding/removing column or row seperators. 
        b- Select the Row/Column merging tool:
            - Edit -> Mark Row/Col Span
            or
            - Ctrl + M
            (Make sure that this is done, otherwise it can cause problems)
        c- Click on a table to select it. (selected table will be highlighted).
        d- To mark two vertically adjacent cells as a single cell, press LEFT mouse button from the top cell and drag the mouse to the bottom cell.
        e- To mark two horizontally adjacent cells as a single cell, press RIGHT mouse button from the left cell and drag the mouse to the right cell.
        f- In case if a mistake is made in merging the cells, repeat step 5.a to re-initialize the cells.
        g- Once you are done with a given table press Ctrl + M to deselect the current table.
        h- Repeat the process for all the tables. (steps 5.c onwards)
    6- Save the ground-truth file:
        - File -> Save Ground Truth 
        or 
        - Ctrl + S
