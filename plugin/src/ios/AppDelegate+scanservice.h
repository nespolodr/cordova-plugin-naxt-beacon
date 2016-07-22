
#import "AppDelegate.h"
#import <EstimoteSDK/EstimoteSDK.h>

@interface AppDelegate (ScanService) <ESTBeaconManagerDelegate>

@property (nonatomic) ESTBeaconManager *beaconManager;

@end
