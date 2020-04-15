       01  PurchaseData.
               05  ItemCount               PIC 99 COMP.
               05  CustomerInitial         PIC X OCCURS 2 TIMES.
               05  CustomerSurname         PIC X(20).
               05  Invoice.
                   10  Item                OCCURS 1 TO 50 TIMES
                                           DEPENDING ON ItemCount.
                       15  ItemType        PIC X.
                           88  Grocery          VALUE "G".
                           88  NonGrocery       VALUE "N".
                       15  ItemGrocery.
                           20  ItemCode        PIC X(10).
                           20  Description     PIC X(30).
                           20  Price           PIC 9999V99.
                           20  SellByDate      PIC 9(8).
                           20  Quantity        PIC 9(6).
                       15  ItemNonGrocery REDEFINES ItemGrocery.
                           20  ItemCode        PIC X(10).
                           20  Description     PIC X(30).
                           20  Price           PIC 9999V99.
                           20  Colour          PIC X(8).
                           20  Quantity        PIC 9(6).
