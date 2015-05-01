from django.conf.urls import patterns, include, url
from django.contrib import admin
from pictures.views import ViewPicture, PuckUp, ShuckIt, ViewComments
from puck_user.views import PuckUser, History, GetOldPic, DeletePic

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'puck_passer.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    url(r'^admin/', include(admin.site.urls)),
    # latitudes and longitudes will be ints. To calculate floats, just
    # divide by 10^8
    url(r'^view/(?P<user>\d{10})/(?P<lat>\d+)/(?P<lon>\d+)/(?P<dist>\d{2})/$', 
                ViewPicture.as_view()),
    url(r'^post/(?P<user>\d{10})/(?P<lat>\d+)/(?P<lon>\d+)/$',
                ViewPicture.as_view()),
    url(r'^authenticate/$',PuckUser.as_view()),
    url(r'^puck_up/(?P<pic_url>[a-zA-Z0-9_. ,]*)/$', PuckUp.as_view()),
    url(r'^shuck_it/(?P<pic_url>[a-zA-Z0-9_. ,]*)/$', ShuckIt.as_view()),

    url(r'^(?P<pic_url>[a-zA-Z0-9_. ,]*)/comments/$', 
                ViewComments.as_view()),

    url(r'^(?P<user>\d{10})/history/$', History.as_view()),
    url(r'^(?P<user>\d{10})/(?P<number>\d+)/$', GetOldPic.as_view()),
    url(r'^delete/(?P<pic_url>[a-zA-Z0-9_. ,]*)/$', DeletePic.as_view()),
    #url(r'^view/(?P<user>\d{10})/(?P<lat>\d+)/(?P<lon>\d+)/(?P<dist>\d{2})/$', include(admin.site.urls)),
)
