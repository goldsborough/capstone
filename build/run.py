#!/usr/bin/env python
# -*- coding: utf-8 -*-

import argparse
import os
import re
import subprocess
import sys

def setup_parser():
	parser = argparse.ArgumentParser(
		description='Build or run the capstone project'
	)

	parser.add_argument(
		'-b',
		'--build',
		action='store_true',
		help='build the project'
	)

	parser.add_argument(
		'-g',
		'--graphical',
		action='store_true',
		help='run the project with a GUI, if possible.'
	)

	parser.add_argument(
		'-t',
		'--terminal',
		action='store_true',
		help='run the from the terminal, if possible.'
	)

	return parser

def handle_directory():
	match = re.match(r'.*capstone', os.getcwd())
	if not match:
		raise RuntimeError('Cannot find path to capstone root!')
	os.chdir(match.group())

def main():

	handle_directory()

	parser = setup_parser()
	args = parser.parse_args(sys.argv[1:])

	if args.build:
		print("Building ...")
		subprocess.call(
			'javac -cp lib/lanterna-2.1.9.jar:source' +
			' source/Main.java',
			shell=True
		)

	if args.terminal:
		print("Starting Terminal ...")
		subprocess.call(
			'java -Djava.awt.headless=true ' +
			'-cp :lib/lanterna-2.1.9.jar:source Main',
			shell=True
		)

	elif args.graphical:
		print("Starting GUI ...")
		subprocess.call(
			'java -cp :lib/lanterna-2.1.9.jar:source Main',
			shell=True
		)

if __name__ == '__main__':
	main()
