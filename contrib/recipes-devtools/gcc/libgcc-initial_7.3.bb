require recipes-devtools/gcc/gcc-${PV}.inc
require libgcc-initial.inc

#autotools_preconfigure_append() {
#do_configure_prepend () {
#	#echo "TEST TEST TEST TEST TEST TEST TEST TEST TEST"
#	#echo `pwd`
#        #for file in $(grep -rl '\-V -qversion' ${S}/../../.. ) ; do
#	for file in $(grep -rl '\-V -qversion' ${S} ) ; do
#                sed -i 's/ -V -qversion/ /g' $file
#        done
#}
