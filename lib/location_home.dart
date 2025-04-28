import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:get/get.dart';
import 'package:get/get_core/src/get_main.dart';
import 'location_controller.dart';

class LocationHome extends StatefulWidget{
  @override
  State<StatefulWidget>createState() => _LocationHome();
}

class _LocationHome extends State<LocationHome>{
  final Locationcontrller controller = Get.put(Locationcontrller());


  @override
  Widget build(BuildContext context) {
    controller.getLastLocation(); // Load last known location
    controller.startLocationService(); // Start service

    return Scaffold(
      appBar: AppBar(title: Text('Location Tracker')),
      body: Center(
        child: Obx(() {
          return Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text('Latitude: ${controller.lat.value}'),
              Text('Longitude: ${controller.long.value}'),
              Text('Timestamp: ${controller.time.value}'),
            ],
          );
        }),
      ),
    );
  }
}
