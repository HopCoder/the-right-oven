from django.db import models

# Designed to keep track of which users have seen what
class User(models.Model):
    number = models.IntegerField()
    comments = models.ForeignKey('Comment', null=True)
    def __str__(self):
        return str(self.number)

# Keep track of comments left on pictures
class Comment(models.Model):
    comment = models.CharField(max_length=256)
    pic = models.ForeignKey('Picture', null=True)
    order = models.IntegerField(default=1)
    def __str__(self):
        return self.comment 

#This will keep track of pictures uploaded
class Picture(models.Model):
    #8 decimal places gets to within 1 mm
    lat = models.DecimalField(max_digits=10,decimal_places=8)
    lon = models.DecimalField(max_digits=11,decimal_places=8)
    #use this to find the statically hosted picture
    unique_code = models.CharField(max_length=50)
    # something to track the user that posted
    poster = models.ForeignKey('User', null=True)
    # add something to track how many views the picture has left
    views_left = models.IntegerField(default=5)
    
    created = models.DateTimeField(auto_now_add=True, null=True)

    # make sure not to show the same picture to the same user twice
    users_seen = models.ManyToManyField('User', related_name='pics_viewed')

    def __str__(self):
        return self.unique_code

