from django.shortcuts import render
from django.views.generic import View
from pictures.models import Picture, User
from django.template import loader, RequestContext
from django.http import HttpResponse, HttpResponseRedirect, HttpResponseNotFound
from puck_passer.settings import BASE_DIR
import os

# Get the user history of photos posted
class History(View):
    def get(self, request, user):
        user = int(user)
        pics = Picture.objects.filter(poster__number=user).order_by('-created')
        template=loader.get_template('history.html')
        context = RequestContext(request, {
                    'history_list':pics,
                })
        print(pics)
        return HttpResponse(template.render(context))

# use this class to view a single pic from the history list
class GetOldPic(View):
    def get(self, request, user, number):
        user = int(user)
        number = int(number)
        pics = Picture.objects.filter(poster__number=user).order_by('-created')
        return HttpResponseRedirect('/static/'+pics[number].unique_code)

# use this to delete a picture once gotten with GetOldPic
class DeletePic(View):
    def get(self, request, pic_url):
        try:
            pic = Picture.objects.get(unique_code=pic_url)
        except:
            return HttpResponseNotFound("<h1>wrong pic url</h1>")
        pic.delete()
        os.remove(BASE_DIR+'/pictures/static/'+pic_url)
        return HttpResponse("<h1>success</h1>")

# Use this to get csrf token
class PuckUser(View):
    def get(self, request):
        return render(request, 'index.html', {})

