from fabric.api import *
from fabric.operations import reboot
import sys

env.hosts = ['192.168.0.2', '192.168.0.3', '192.168.0.4']
env.user = 'qmat'

def restart_machines():
    try:
        reboot(1)
    except:
        print "couldn't restart machine %s" % env.host_string

def shutdown_machines():
    try:
        sudo('shutdown -h now')
    except:
        print "couldn't restart machine %s" % env.host_string
    

'''
if __name__ == '__main__':
    if len(sys.argv) > 1:
        if sys.argv[1] == 'reboot':
            restart_machines()
        if sys.argv[1] == 'shutdown':
            shutdown_machines()
'''
