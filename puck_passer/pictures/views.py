from django.shortcuts import render
from .models import Picture, User, Comment
from django.http import HttpResponseRedirect, HttpResponseNotFound, HttpResponse
from django.template import loader, RequestContext
from django.templatetags.static import static
from django.core.urlresolvers import reverse
from django.views.generic import View
import math
import random
import time
from django.views.decorators.csrf import csrf_exempt
import os

class PuckUp(View):
    def get(self, request, pic_url):
        pic = Picture.objects.get(unique_code=pic_url)
        pic.views_left += 5
        return HttpResponse("<h1>Great Success!</h1>")



random.seed()

class ViewPicture(View):
    def get_range(self, lat, lon, dist):
        # TODO: test to see if distances are calculated correctly
        max_lat = lat + (dist*1600/6371000)*180/math.pi
        min_lat = lat - (dist*1600/6371000)*180/math.pi
        
        max_lon = (lon + 2 * 
                math.asin(
                    math.sin(dist * 1600/(2*6371000))/
                    math.cos(lat))
                * 180/math.pi)
        min_lon = (lon - 2 *
                math.asin(
                    math.sin(dist * 1600/(2*6371000))/
                    math.cos(lat))
                * 180/math.pi)

        # TODO: add bound checking
        
        if max_lat < min_lat: min_lat, max_lat = max_lat, min_lat
        if max_lon < min_lon: min_lon, max_lon = max_lon, min_lon

        # DEBUGGING:
        #print('(', min_lat, ',', min_lon, '),(', max_lat, ',', max_lon, ')')

        photos = Picture.objects.filter(lat__lte=max_lat, lat__gte=min_lat,
                                        lon__lte=max_lon, lon__gte=min_lon,
                                        views_left__gt=0)
        return photos
    
    def filter_list(self, user, photos):
        photos = photos.exclude(users_seen__number=user)
        return photos

    def select_pic(self, photos):
        # how to sort list? right now just choose the first one
        # We will choose a random one.... I think its better that way
        return photos[random.randint(0,len(photos)-1)]

    def user_seen(self, user, photo):
        try:
            viewer = User.objects.get(number=user)
        except:
            viewer = User(number=user)
            viewer.save()
        photo.users_seen.add(viewer)
        photo.views_left -= 1
        return

    def get(self, request, user, lat, lon, dist):
        # TODO: this does not handle negative values for requests
        lat = int(lat)/(10**8)
        lon = int(lon)/(10**8)
        dist = int(dist)
        user = int(user)
        display_pics = self.get_range(lat, lon, dist)
        display_pics = self.filter_list(user, display_pics)
        if len(display_pics) == 0:
            return HttpResponseNotFound("<h1>No more pictures</h1>")
        final_pic = self.select_pic(display_pics)
        self.user_seen(user, final_pic)
        return HttpResponseRedirect(static(final_pic.unique_code))

    def post(self, request, user, lat, lon):
        # Unique code will be in this format:
        # [10 digit phone number][time since epoch]
        # eg: 20128373371427929704
        # corresponds to my # and 7:08 on April 1st 2015
        unique_code = user + str(int(time.mktime(time.localtime()))) + '.png'
        lat = int(lat)/(10**8)
        lon = int(lon)/(10**8)
        user = int(user)

        new_pic = Picture()
        new_pic.lat = lat
        new_pic.lon = lon
        new_pic.unique_code = unique_code
        # something to identify user to picture
        try:
            poster = User.objects.get(number=user)
        except:
            poster = User(number=user)
            poster.save()

        new_pic.poster = poster

        line_one = request.readline()
        index = line_one.find(b'&')
        
        try:
            if index == -1:
                raise EOFError('No picture data')
            f = open('./pictures/static/'+unique_code, 'wb')
            f.write(line_one[index+1:])
            for line in request.readlines():
                f.write(line)

            f.close()
            new_pic.save()

            return HttpResponse("<h1>Finished Uploading</h1>")
       
        except EOFError:
            return HttpResponseNotFound("<h1>No data..."+
                    " (or incorrect format)</h1>")
       
class ViewComments(View):
    def get(self, request, pic_url):
        comment_list = Comment.objects.filter(pic__unique_code=pic_url)
        comment_list.order_by('order')
        template = loader.get_template('comments.html')
        context = RequestContext(request, {
                    'comment_list':comment_list,
                })
        return HttpResponse(template.render(context))

    def post(self, request, pic_url):
        try:
            comments = Comment.objects.filter(pic__unique_code=pic_url)
            comments.order_by('order')
            number = comments[-1].order + 1
        except:
            number = 1

        try:
            new_comment = Comment(comment=request.POST['comment'], order=number,
                              pic=Picture.objects.get(unique_code=pic_url))
        except Exception as err:
            print(err)
            return HttpResponse("<h1>Comment Upload Failed</h1>")

        new_comment.save()
        return HttpResponse("<h1>Done</h1>") 

