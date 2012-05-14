from fabric.api import *
from fabric.operations import reboot
import sys

env.hosts = ['192.168.0.2', '192.168.0.3', '192.168.0.4']
env.user = 'qmat'

def restart_machines():
    reboot(1)

def shutdown_machines():
    sudo('shutdown -h now')

'''
if __name__ == '__main__':
    if len(sys.argv) > 1:
        if sys.argv[1] == 'reboot':
            restart_machines()
        if sys.argv[1] == 'shutdown':
            shutdown_machines()
'''
