#!/bin/bash
function branch {
  br=`git branch | grep "*"`
  echo ${br/* /}
}
var=$(branch)
echo `git add .`
echo `git commit -m $1`
echo `git fetch && git rebase` 
echo `git push origin  $var`
echo `git fetch && git rebase`
