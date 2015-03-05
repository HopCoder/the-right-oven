from django.conf.urls import patterns, include, url
from django.contrib import admin
from pictures.views import ViewPicture

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'puck_passer.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    url(r'^admin/', include(admin.site.urls)),
    url(r'^view/(?P<user>\)/(?P<lat>\)/(?P<lon>\)/(?<dist>\d{2})/$', 
                ViewPicture.as_view()),
)
