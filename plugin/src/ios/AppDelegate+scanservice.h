
#import "AppDelegate.h"
#import <EstimoteSDK/EstimoteSDK.h>

@interface AppDelegate () <ESTBeaconManagerDelegate>

- (id) getCommandInstance:(NSString*)className;

@property (nonatomic) ESTBeaconManager *beaconManager;

@end
