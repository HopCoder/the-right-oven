from django.db import models

class User(models.Model):
    number = models.IntegerField()
    
    def __str__(self):
        return str(self.number)

class Picture(models.Model):
    #8 decimal places gets to within 1 mm
    lat = models.DecimalField(max_digits=10,decimal_places=8)
    lon = models.DecimalField(max_digits=11,decimal_places=8)
    unique_code = models.CharField(max_length=50)
    # something to track the user that posted
    poster = models.ForeignKey(User, null=True)
    # add something to track how many views the picture has left
    views_left = models.IntegerField(default=5)
    
    users_seen = models.ManyToManyField(User, related_name='pics_viewed')

    def __str__(self):
        return self.unique_code

