#!/usr/bin/env python
# -*- coding: utf-8 -*-

import jsonpickle


class PrettyPrinter:
    def __init__(self, indent):
        pass

    def pprint(self, obj):
        if obj is None:
            print '{}'
        else:
            print jsonpickle.encode(obj, unpicklable=False)

