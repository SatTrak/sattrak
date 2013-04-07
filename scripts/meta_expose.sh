#!/bin/bash

#File information
FILETYPE="png"
FILEDIR=*.$FILETYPE

#echo "$FILEDIR"
shopt -s nullglob

# Check if both arguments (start and end) are provided
if [ ${#@} != 2 ]; then
  echo "Need a Start and End time argument"
  echo "format should be YYYY_MM_DD-HH:mm:ss:mss"
  exit
fi 

# get the two arguments
start=$1
end=$2

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

# Make a counter and file array
counter=0
files[0]=0

# For each file in the file directory
# process and compare to see if it should be added
# to list of files to be averaged
for f in $FILEDIR
do
  echo ""
  echo "Processing '$f' "
	echo ""  

  # Split into an array of values
  ar=(${f//[_-:.]/ })

  # if the filename does not have 8 elements, do not process
  # The elements should be, in this order,
  # Year Month Day Hours Minutes Seconds Milliseconds FileExtension
  if [ ${#ar[@]} != 8 ]; then
    echo "Not a valid filename"
		echo ""
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
    if [ $start_year -le $year -a $start_month -le $month -a $start_day -le $day -a $start_hours -le $hours -a $start_minutes -le $minutes -a $start_seconds -le $seconds -a $start_milsecs -le $milsecs ]
    then
    
      echo " After the start time "
      # Check if before end
      if [ $end_year -ge $year -a $end_month -ge $month -a $end_day -ge $day -a $end_hours -ge $hours -a $end_minutes -ge $minutes -a $end_seconds -ge $seconds -a $end_milsecs -ge $milsecs ]
      then
	   
        echo " Before the end time "
        # Add files to array
        echo "File added"
        files[counter]="$f"
        let counter=counter+1
				echo "counter = $counter"
				echo ""
      
			fi
    fi        
  fi
done # End of file finding loop

# Average the files
echo "Averaging files: ${files[@]}"
echo ""
convert "${files[@]}" -average out."$FILETYPE"

