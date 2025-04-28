import 'package:flutter/services.dart';
import 'package:get/get.dart';
import 'package:get/get_state_manager/src/simple/get_controllers.dart';

class Locationcontrller extends GetxController{
  static const platform = MethodChannel('FATCH_LOACTION');

  var lat = 0.0.obs;
  var time = ''.obs;
  var long = 0.0.obs;


  Future<void> getLastLocation() async {
    try {
      final result = await platform.invokeMethod('getlastLocation');
      lat.value = result['latitude'];
      long.value = result['longitude'];
      time.value = result['timestamp'];
    } catch (e) {
      print("Error : $e");
    }
  }

  Future<void> startLocationService() async {
    try {
      await platform.invokeMethod('startlocationService');
    } catch (e) {
      print("Error: $e");
    }
  }

}