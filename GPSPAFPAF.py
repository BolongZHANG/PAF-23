# -*- coding: utf-8 -*-
"""
Created on Tue Jun 20 15:16:07 2017

@author: ArthurGarnier
"""

import gpxpy

from math import *
 
gpx_file = open('/Users/arthurbg/Downloads/melu-2.gpx', 'r')
gpx = gpxpy.parse(gpx_file)

points=gpx.tracks[0].segments[0].points

import folium
from folium.features import CustomIcon

#on fabrique notre propre pointeur en forme de chat 
icon_image = '/Users/arthurbg/Desktop/anaconda/lib/python3.5/site-packages/gmplot/markers/logo.png'

icon = CustomIcon(
    icon_image,
    icon_size=(38,38 ),
    icon_anchor=(10, 40)
)

map_osm = folium.Map(location=[46.44, 0.12])
#HTML('map_osm')
#from pyensae import folium_html_map
for i,point in enumerate(points[1:100]):
   #print(points[1].speed_between(points[2]))
   #print('Point at ({0},{1},{2}) : {3}'.format(point.latitude, point.longitude, point.elevation,point.time))

   #map_osm.add_child(folium.CircleMarker(location=[point.latitude, point.longitude], popup='a',fill_color='red', radius=2))
   #map_osm.add_child(folium.CircleMarker(location=[point.latitude, point.longitude], popup='a',fill_color='blue', radius=2))
   if (i == len(points[1:100]) - 1):
       #map_osm.simple_marker([point.latitude, point.longitude])
       #folium.Marker([point.latitude, point.longitude], popup='votre matou est ici',icon = folium.Icon(color ='green')).add_to(map_osm)
       marker = folium.Marker(location=[point.latitude, point.longitude], icon=icon, popup='Antenne bouygues telecom ')
       map_osm.add_child(marker)


map_osm.save('o.html')
## on demande à l'utilisateur de donner une limite et les coordonées gps de la référence



from math import radians, cos, sin, asin, sqrt
def haversine(lat1, lon1, lat2, lon2):
    # la formule de haversine permet de calculer des distance à partir des latitude et longitude
    lon1, lat1, lon2, lat2 = map(radians, [lon1, lat1, lon2, lat2]) 
    dlon = lon2 - lon1 
    dlat = lat2 - lat1 
    a = sin(dlat/2)**2 + cos(lat1) * cos(lat2) * sin(dlon/2)**2
    c = 2 * asin(sqrt(a)) 
    m = 6367 * c * 1000
    return m
    




import gmplot


gmap = gmplot.GoogleMapPlotter(46.439882,0.11577,16)


gmap.scatter([46.43991,46.43981,46.43971,46.43961], [0.115835,0.115845,0.115855,0.115865], '#logo2',size = 1, marker=True)

gmap.draw("test34.html")
