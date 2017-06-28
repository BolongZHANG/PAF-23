# -*- coding: utf-8 -*-
"""
Created on Tue Jun 20 15:16:07 2017

@author: ArthurGarnier
"""

import gpxpy

from math import *
 
#gpx_file = open('/Users/arthurbg/Downloads/melu-2.gpx', 'r')
#gpx = gpxpy.parse(gpx_file)

#points=gpx.tracks[0].segments[0].points

import folium
from folium.features import CustomIcon
Lat=[]
Long=[]
import codecs
f = codecs.open('/Users/arthurbg/Downloads/sup_support.csv', 'r', 'UTF-8')
dataset=[]
for line in f:
        dataset.append(list(line))
for i in range(1,len(dataset)):
    j=0
    while dataset[i][j] != ';' :
        j+=1
    a = ''
    k=0
    l=0
    while(dataset[i][j+1+k] != ',') :
        a+=str(dataset[i][j+1+k])
        k+=1
    b=''
    while(dataset[i][j+1+k+2+l] != ';') :
        b+=str(dataset[i][j+1+k+2+l])
        l+=1
    if 48.813<float(a)<48.899:
        if 2.25<float(b)<2.45:
            Long.append(float(b))
            Lat.append(float(a))
#48.853 latitude
# 2.35 longitude
#on fabrique notre propre pointeur en forme de chat 
icon_image = '/Users/arthurbg/Desktop/anaconda/lib/python3.5/site-packages/gmplot/markers/logo-2.png'

o2 = folium.features.CustomIcon(
    icon_image,
    icon_size=(38,38 ),
    icon_anchor=(10, 40)
)

map_osm = folium.Map(location=[48.8534, 2.35],zoom_start=17)
#HTML('map_osm')
#from pyensae import folium_html_map
print(len(Lat))
for i in range(len(Lat)):
   map_osm.add_child(folium.CircleMarker(location=[Lat[i],Long[i]], popup='a',fill_color='red', radius=10))
map_osm.save('paf1.html')
## on demande à l'utilisateur de donner une limite et les coordonées gps de la référence



#from math import radians, cos, sin, asin, sqrt
#def haversine(lat1, lon1, lat2, lon2):
#    # la formule de haversine permet de calculer des distance à partir des latitude et longitude
#    lon1, lat1, lon2, lat2 = map(radians, [lon1, lat1, lon2, lat2]) 
#    dlon = lon2 - lon1 
#    dlat = lat2 - lat1 
#    a = sin(dlat/2)**2 + cos(lat1) * cos(lat2) * sin(dlon/2)**2
#    c = 2 * asin(sqrt(a)) 
#    m = 6367 * c * 1000
#    return m
#    
#
#
#
#
import gmplot


gmap = gmplot.GoogleMapPlotter(48.853,2.35,16)
#gmap = gmplot.from_geocode("Paris")

#gmap.scatter(Lat,Long, '#logo2',size = 1, marker=True)
gmap.scatter(Lat,Long, '#FFFAFA',size = 1, marker=True)

gmap.draw("paf2.html")

