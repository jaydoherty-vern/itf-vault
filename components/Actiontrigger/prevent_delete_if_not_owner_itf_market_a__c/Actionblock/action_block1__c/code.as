IF
Id(ownerid__v) != @User.id
THEN
CancelOperation("You do not have the correct permissions to delete this record. Contact your administrator for help.");