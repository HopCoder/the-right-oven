from django.shortcuts import render
from .models import Picture, User
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
                                        lon__lte=max_lon, lon__gte=min_lon)
        return photos
    
    def filter_list(self, user, photos):
        # TODO: still need to add logic for filtering out if user has already seen
        # I think we might be able to do it in the filter, in which case it will
        # probably be faster.  Try that first
        return

    def select_pic(self, photos):
        # how to sort list? right now just choose the first one
        # We will choose a random one.... I think its better that way
        return photos[random.randint(0,len(photos)-1)]

    def user_seen(self, user, photo):
        # TODO: add user to the list of people that have seen this picture
        return

    def get(self, request, user, lat, lon, dist):
        # TODO: this does not handle negative values for requests
        lat = int(lat)/(10**8)
        lon = int(lon)/(10**8)
        dist = int(dist)
        user = int(user)
        display_pics = self.get_range(lat, lon, dist)
        self.filter_list(user, display_pics)
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

        new_pic = Picture()
        new_pic.lat = lat
        new_pic.lon = lon
        new_pic.unique_code = unique_code
        #TODO: add something to identify user to picture
        new_pic.save()

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

            return HttpResponse("<h1>Finished Uploading</h1>")
       
        except EOFError:
            return HttpResponseNotFound("<h1>No data..."+
                    " (or incorrect format)</h1>")
        
