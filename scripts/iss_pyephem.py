import math
import time
from datetime import datetime, timedelta
import ephem

# K Wood, modified version from: http://www.sharebrained.com/2011/10/18/track-the-iss-pyephem/

# Always get the latest ISS TLE data from:
# (K. Wood) --> NASA's human spaceflight page is a bit verbose, the celestrak one is simpler, but less accurate...
# http://spaceflight.nasa.gov/realdata/sightings/SSapplications/Post/JavaSSOP/orbit/ISS/SVPOST.html
# OR
# http://www.celestrak.com/NORAD/elements/stations.txt

TLE1 = "1 25544U 98067A   13104.86288288  .00014856  00000-0  24654-3 0  6129"
TLE2 = "2 25544  51.6469  62.5942 0010689 141.3178 311.5110 15.52291388824875"

# my house, don't forget to input your coordinates...
home = ephem.Observer()
home.lat= '40.972975'
home.lon = '-76.886064'
home.elevation = 134 # meters

iss = ephem.readtle('ISS', TLE1,TLE2 )

while True:

   # home.date = datetime.utcnow()
	
    #iss.compute(home)
    # is the object eclipsed by Earth (and therefore non-observable)
    #if iss.eclipsed:
    #    print 'now ISS is currently eclipsed'
    #else:
    #    print 'now iss: altitude %4.1f deg, azimuth %5.1f deg' % ( math.degrees( iss.alt) , math.degrees( iss.az ) ) 
		
    now = datetime.utcnow()
    home.date = now.replace(hour=22, minute=1, second=29);
	
    iss.compute(home)
    # is the object eclipsed by Earth (and therefore non-observable)
    if iss.eclipsed:
	print '10 min ISS is currently eclipsed'
    else:
	print '10 min iss: altitude %4.1f deg, azimuth %5.1f deg' % ( math.degrees( iss.alt) , math.degrees( iss.az ) ) 
		
    time.sleep(1.0)
    print '\n'
