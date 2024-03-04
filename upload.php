<?php
    if (is_uploaded_file($_FILES["uploadedfile"]["tmp_name"])) {
        if (move_uploaded_file($_FILES["uploadedfile"]["tmp_name"], "C:\upload\\".$_FILES["uploadedfile"]["name"])) {
            print("Success!");
        } else {
            print("Cannot move file");
        }
    }
    else {
        print("Cannot upload file");
    }
?>