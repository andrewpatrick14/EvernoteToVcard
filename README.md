# EvernoteToVcard
A utility to convert Evernote Business Cards to VCard format

This a small utility to convert notes exported from evernote which contain
scanned business cards to vcard format which
are compatible with most contact management applications. 

You need have a JDK (or JRE) of Java 17 or above installed for this work.
This can be downloaded from several places amongst them:

https://adoptium.net/en-GB/temurin/releases/

To use the utility you will first need to export the notes you want to convert
to a single .enex file. There are instructions on how to do this on Evernote's help
pages. The utility should ignore notes that don't contain business cards, or any
additional non-business card info in the note, however it hasn't been widely 
tested on notes that contain non-business card data. 

To launch the program (assuming the java bin directory is on your path)

    java -jar EclipseToVCard.jar enexfile outputdir [options]

The default behaviour is to read in the enex file and write a separate .vcf file
for each business card to the given output directory. The names of the vcf files will
be taken from the names of the contacts, with any non filename valid characters removed, and
numbers added to ensure each file has a unique name. 

The utility will process all the usual business card data, including any profile photo. It will
correctly handle labels for email addresses and telephone numbers ('home', 'work' etc).

There is no common standard for exchanging labels for websites - so this information may
be lost in the translation. The export of social media handles uses the non standard
X-SOCIALPROFILE vCard tag. This works for ios contacts, but seems not to work for google or
windows contacts. A future version may add support different output options. 

The utility can save copies of the scanned business card images in the directory as well (the 
VCard standard doesn't have a way of storing card images). This is enabled by the -d option.

There are two other output options -c name which will output a single comma separated
values file with the given name, one line for each contact. The header row takes the column
names from the evernote business card fields. 

The option -s name will output all the contacts to a single large vcf file. 




