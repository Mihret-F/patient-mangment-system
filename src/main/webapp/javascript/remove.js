/**
 * 
 */
    function removePatient(button) {
        // Traverse up the DOM to find the row and remove it
        var row = button.parentNode.parentNode;
        row.parentNode.removeChild(row);
    }
