from django.db import models


class Picture(models.Model):
    #8 decimal places gets to within 1 mm
    lat = models.DecimalField(max_digits=10,decimal_places=8)
    lon = models.DecimalField(max_digits=11,decimal_places=8)
    unique_code = models.CharField(max_length=50)
    #TODO: add something to track the user that posted
    
    def __str__(self):
        return self.unique_code

class User(models.Model):
    number = models.IntegerField()
    viewed_pics = models.ManyToManyField(Picture)
    
    def __str__(self):
        return str(self.number)

