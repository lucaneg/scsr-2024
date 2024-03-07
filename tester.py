#!/usr/bin/env python3
import os
import shutil
import subprocess
import sys
import pandas

if len(sys.argv) != 3 and (len(sys.argv) != 2 and sys.argv[1] == 'cleanup'):
	print('name of the test to execute and output folder missing')
	exit()

logdir = 'eval-logs'
if os.path.isdir(logdir):
	shutil.rmtree(logdir)

os.mkdir(logdir)
with open(logdir + '/.gitignore', 'w') as gitignore:
	gitignore.write('*.json')

df = pandas.DataFrame({'branch': [], 'id': [], 'compile': [], 'test': []})

res = subprocess.run(['git', 'for-each-ref', "--format='%(refname:lstrip=3)'", 'refs/remotes/origin/'], capture_output=True)

for source in res.stdout.decode().split('\n'):
	if source == '':
		continue
	if source[0] == '\'' and source[-1] == '\'':
		source = source[1:-1]
	if source == 'master':
		continue

	pid = source.split('-')[0]
	position = len(df.index)
	df.loc[position] = [source, pid, 0, 0]

	if sys.argv[1] == 'cleanup':
		subprocess.run(['git', 'branch', '-D', source])
		continue 

	logfile = logdir + '/' + source + '.log'
	with open(logfile, 'w') as log:
		log.write('###############################################################################')
		log.write('CHECKOUT')
		log.write('###############################################################################')
		print(f'+ checking out branch: {source}')
		res = subprocess.run(['git', 'checkout', source], stdout = log, stderr = log)
		if res.returncode != 0:
			print(f'###### checkout failed on {source}')
			continue

		log.write('###############################################################################')
		log.write('MERGE')
		log.write('###############################################################################')
		print(f'++ merging master into {source}')
		my_env = os.environ.copy()
		my_env['GIT_MERGE_AUTOEDIT'] = 'no'
		res = subprocess.run(['git', 'merge', '-Xtheirs', 'master'], stdout = log, stderr = log, env=my_env)
		if res.returncode != 0:
			print(f'###### merge failed on {source}')
			continue

		log.write('###############################################################################')
		log.write('BUILD')
		log.write('###############################################################################')
		print(f'++ building {source}')
		res = subprocess.run(['./gradlew', 'assemble'], stdout = log, stderr = log)
		if res.returncode != 0:
			print(f'###### build failed on {source}')
			continue
		df.loc[position, 'compile'] = 1

		log.write('###############################################################################')
		log.write('TEST')
		log.write('###############################################################################')
		print(f'++ testing {source}')
		res = subprocess.run(['./gradlew', 'test', '--tests', sys.argv[1]], stdout = log, stderr = log)
		if res.returncode != 0:
			print(f'###### test failed on {source}')
			continue
		df.loc[position, 'test'] = 1

		log.write('###############################################################################')
		log.write('COPY')
		log.write('###############################################################################')
		outdir = 'outputs/' + sys.argv[2]
		if os.path.isdir(outdir):
			print(f'++ copying output files')
			shutil.copytree('outputs/' + sys.argv[2], logdir + '/' + source)
		else:
			print(f'++ no output files to copy')

		log.write('###############################################################################')
		log.write('RESET')
		log.write('###############################################################################')
		print(f'++ resetting {source}')
		res = subprocess.run(['git', 'reset', '--hard'], stdout = log, stderr = log)
		if res.returncode != 0:
			print(f'###### reset failed on {source}')
			continue
		res = subprocess.run(['git', 'clean', '-fd'], stdout = log, stderr = log)
		if res.returncode != 0:
			print(f'###### clean failed on {source}')
			continue


print('+ checking out master')
subprocess.run(['git', 'checkout', 'master'])

fname = 'report.csv'
if sys.argv[1] == 'cleanup':
	if os.path.isfile(fname):
		os.remove(fname)
		print('deleted', fname)
else:
	df.to_csv(fname)
	print('created', fname)
