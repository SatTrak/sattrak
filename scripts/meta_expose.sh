#!/bin/bash

FILETYPE="jpg"
FILEDIR=*.$FILETYPE

#echo "$FILEDIR"
shopt -s nullglob

# get the two arguments to the expose script
# Examples of arguments
#   2013_04_01-20:13:32:033
#   2013_04_01-20:13:33:000
start = $1
end = $2

# parse before and after into arrays
start_ar=(${start//[_-:]/ })
end_ar=(${end//[_-:]/ })

# if argument 1 is not valid, exit
if [ ${#start_ar[@]} != 7 ]; then
  echo "Start argument not valid"
  exit
fi

# if argument 2 is not valid, exit
if [ ${#end_ar[@]} != 7 ]; then
  echo "End argument not valid"
  exit
fi


# make variables from array
start_year=${start_ar[0]}
start_month=${start_ar[1]}
start_day=${start_ar[2]}
start_hours=${start_ar[3]}
start_minutes=${start_ar[4]}
start_seconds=${start_ar[5]}
start_milsecs=${start_ar[6]}

end_year=${end_ar[0]}
end_month=${end_ar[1]}
end_day=${end_ar[2]}
end_hours=${end_ar[3]}
end_minutes=${end_ar[4]}
end_seconds=${end_ar[5]}
end_milsecs=${end_ar[6]}


counter = 0

# For each file in the file directory
# process and compare to see if it should be added
# to list of files to be averaged
for f in $FILEDIR
do
  echo ""
  echo "Processing '$f' "
  
  # Split into an array of values
  ar=(${f//[_-:.]/ })

  #echo ${#ar[@]}

  # if the filename does not have 8 elements, do not process
  # The elements should be, in this order,
  # Year Month Day Hours Minutes Seconds Milliseconds FileExtension
  if [ ${#ar[@]} != 8 ]; then
    echo "Not a valid filename"
  else
    # Parse out all information
    year=${ar[0]}
    month=${ar[1]}
    day=${ar[2]}
    hours=${ar[3]}
    minutes=${ar[4]}
    seconds=${ar[5]}
    milsecs=${ar[6]}
    type=${ar[7]}

    # echo for debugging
    echo "Year = $year"
    echo "Month = $month"
    echo "Day = $day"
    echo "Hours = $hours"
    echo "Minutes = $minutes"
    echo "Seconds = $seconds"
    echo "Mseconds = $milsecs"
    echo "Filetype = $type"
    
    # Check if after the start
    if[ (before_year<=year) & (before_month<=month) & (before_day<=day) &
	(before_hours<=hours) & (before_minutes<=minutes) & 
	(before_seconds<=seconds) & (before_milsecs<=milsecs) ]; then
    
      # Check if before end
      if[ (end_year>=year) & (end_month>=month) & (end_day>=day) &
	  (end_hours>=hours) & (end_minutes>=minutes) & 
	  (end_seconds>=seconds) & (end_milsecs>=milsecs) ]; then
	   
        # Add files to array
        echo "File added"
	files[$counter] = $f

      else
          # Not before the ending time
          # Do nothing with this file
      fi

    else
	# Not after the starting time
        # Do nothing with this file
    fi    
    
    
  fi #end file finding loop
  
  # Average the files
  echo files[@]
  convert *.jpg -average out.jpg  

done



