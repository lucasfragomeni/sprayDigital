#!/usr/bin/perl
#
# Usage:
#
# perl rename_old_saves.pl <new_prefix>
#
# if new_prefix is empty, SprayDigital_ will be used
$a=$ARGV[0]?$ARGV[0]:"SprayDigital_";while(<test*.jpg>){$b=$_;s/^test(\d{10})(\d{3}).jpg$/${a}\1_\2.jpg/;rename$b,$_;}
