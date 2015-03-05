from django.shortcuts import render
from .models import Picture, User
from django.http.Http404
from django.templatetags.static import static
from django.core.urlresolvers import reverse

class ViewPicture(View):
    def get(self, request, user, lat, lon, dist):
        # TODO: add calculation for a lat/lon range instead of just an equality
        display_pics = Picture.objects.filter(lat=lat, lon=lon)
        # TODO: still need to add logic for filtering out if user has already seen
        if len(display_pics) == 0:
            return HttpResponseNotFound("<h1>No more pictures</h1>")
        # TODO: how to sort list? right now just choose the first one
        final_pic = display_pics[0]
        # TODO: add user to the list of people that have seen this picture
        return HttpResponseRedirect(static(final_pic.unique_code))
