from django.shortcuts import render
from django.views.generic import View
from pictures.models import Picture, User
from django.template import loader, RequestContext

# Create your views here.
class History(View):
    def get(self, request, user):
        user = int(user)
        pics = Picture.objects.filter(poster__number=user).order_by('-created')
        template=loader.get_template('history.html')
        context = RequestContext(request, {
                    'history_list':pics,
                })
        return HttpResponse(template.render(context))

class GetOldUser(View):
    def get(self, request, user, number):
        user = int(user)
        number = int(number)
        pics = Picture.objects.filter(poster__number=user).order_by('-created')
        return HttpResponseRedirect('/'+pics[number].unique_code+'/comments/')

class PuckUser(View):
    def get(self, request):
        return render(request, 'index.html', {})

