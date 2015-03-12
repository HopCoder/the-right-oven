from django.shortcuts import render
from .models import Picture, User
from django.http import Http404, HttpResponseRedirect, HttpResponse
from django.template import loader, RequestContext
from django.templatetags.static import static
from django.core.urlresolvers import reverse
from django.views.generic import View

class ViewPicture(View):
    def get(self, request, user, lat, lon, dist):
        lat = int(lat)/(10**8)
        lon = int(lon)/(10**8)
        dist = int(dist)
        user = int(user)
        # TODO: add calculation for a lat/lon range instead of just an equality
        display_pics = Picture.objects.filter(lat=lat, lon=lon)
        # TODO: still need to add logic for filtering out if user has already seen
        if len(display_pics) == 0:
            return HttpResponseNotFound("<h1>No more pictures</h1>")
        # TODO: how to sort list? right now just choose the first one
        final_pic = display_pics[0]
        # TODO: add user to the list of people that have seen this picture
        return HttpResponseRedirect(static(final_pic.unique_code))
        #return HttpResponse(loader.get_template('base.html').render(RequestContext(request,{})))

