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
icon_image = '/Users/arthurbg/Desktop/Unknown.png'

icon = CustomIcon(
    icon_image,
    icon_size=(38,38 ),
    icon_anchor=(10, 40)
)

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


gmap.scatter([46.43991,46.43981,46.43971,46.43961], [0.115835,0.115845,0.115855,0.115865], '#FFFAFA',size = 1, marker=True)

gmap.draw("mymappaf.html")
